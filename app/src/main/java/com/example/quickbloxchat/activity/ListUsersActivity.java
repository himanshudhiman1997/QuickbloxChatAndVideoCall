package com.example.quickbloxchat.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quickbloxchat.ChatHelper;
import com.example.quickbloxchat.ItemClickListener;
import com.example.quickbloxchat.adapter.ListUsersAdapter;
import com.example.quickbloxchat.QBUsersHolder;
import com.example.quickbloxchat.R;
import com.example.quickbloxchat.managers.DialogsManager;
import com.example.quickbloxchat.qb.QbDialogHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.ui.dialog.ProgressDialogFragment;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class ListUsersActivity extends AppCompatActivity implements View.OnClickListener, ItemClickListener {

    private RecyclerView recyclerView;
    private Button createChatButton;

    private ArrayList<QBUser> qbUserWithoutCurrent;

    private ArrayList<QBUser> bothUsersList = new ArrayList<>();

    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;

    public static final String EXTRA_QB_USERS = "qb_users";

    private DialogsManager dialogsManager = new DialogsManager();

    private QBSystemMessagesManager systemMessagesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        initViews();
        setClicks();

        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        retrieveAllUser();
    }

    private void retrieveAllUser() {
        QBUsers.getUsers(null, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUsersHolder.getInstance().putUsers(qbUsers);
                qbUserWithoutCurrent = new ArrayList<>();

                for (QBUser qbUser: qbUsers)
                {
                    //
                    //if(!qbUser.getLogin().equals(QBChatService.().getUser().getLogin()))
                    {
                        qbUserWithoutCurrent.add(qbUser);
                    }
                }

                ListUsersAdapter listUsersAdapter = new ListUsersAdapter(ListUsersActivity.this, qbUserWithoutCurrent);
                recyclerView.setLayoutManager(new LinearLayoutManager(ListUsersActivity.this));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(listUsersAdapter);
                listUsersAdapter.setItemClickListener(ListUsersActivity.this);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

    private void setClicks() {
    }

    private void initViews() {
        recyclerView = findViewById(R.id.users_list_recycler_view);
    }


    @Override
    public void onClick1(View view, int position) {
        ChatActions(position);
    }


    private void ChatActions(final int pos) {


        QBUser qbUser = qbUserWithoutCurrent.get(pos);
//        bothUsersList.clear();
//        //bothUsersList.add(QBChatService.getInstance().getUser());
//        bothUsersList.add(qbUser);
//
//        if (isPrivateDialogExist(bothUsersList)) {
//            bothUsersList.remove(ChatHelper.getCurrentUser());
//            QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(bothUsersList.get(0));
//
//            ChatActivity.startForResult(ListUsersActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
//            bothUsersList.clear();
//        } else {
//            ProgressDialog.show(ListUsersActivity.this, "Loading", "Loading Chat...");
//            createPrivateChat();
//            bothUsersList.clear();
//        }

        bothUsersList.add(QBChatService.getInstance().getUser());

        bothUsersList.add(qbUser);

        if (isPrivateDialogExist(bothUsersList)) {
            bothUsersList.remove(ChatHelper.getCurrentUser());
            QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(bothUsersList.get(0));

            ChatActivity.startForResult(ListUsersActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
            bothUsersList.clear();
        } else {
            ProgressDialogFragment.show(getSupportFragmentManager(), R.string.loading_chat);
            createDialog(bothUsersList);
            bothUsersList.clear();
        }

    }


    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers) {
        List<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.addAll(allSelectedUsers);
        selectedUsers.remove(ChatHelper.getCurrentUser());
        return selectedUsers.size() == 1 && QbDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));
    }

    private void createDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {

                        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                        ChatActivity.startForResult(ListUsersActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, dialog);
                        ProgressDialogFragment.hide(getSupportFragmentManager());


                    }

                    @Override
                    public void onError(QBResponseException e) {

                        ProgressDialogFragment.hide(getSupportFragmentManager());

                    }
                }
        );
    }

    private void createPrivateChat() {
        final ProgressDialog progressDialog = new ProgressDialog(ListUsersActivity.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        QBUser[] s = bothUsersList.toArray(new QBUser[bothUsersList.size()]);
        QBChatDialog qbChatDialog = DialogUtils.buildDialog(s);

        QBRestChatService.createChatDialog(qbChatDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(ListUsersActivity.this, "Chat dialog created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Log.e("Error", e.getMessage());
            }
        });



    }

    @Override
    public void onClick(View v) {

    }
}
