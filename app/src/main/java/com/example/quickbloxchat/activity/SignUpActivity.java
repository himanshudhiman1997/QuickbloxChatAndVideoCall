package com.example.quickbloxchat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quickbloxchat.R;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button submitButton, cancelButton;
    private EditText userNameEditText, passEditText, fullNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        setClicks();

        registerSessions();
    }

    private void registerSessions() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClicks() {
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void initViews() {
        submitButton = findViewById(R.id.signup_btnLogin);
        cancelButton = findViewById(R.id.signup_btnCancel);

        userNameEditText = findViewById(R.id.signup_editLogin);
        passEditText = findViewById(R.id.signup_editPassword);
        fullNameEditText = findViewById(R.id.signup_editFullName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signup_btnCancel:
                finish();
                break;

            case R.id.signup_btnLogin:
                String userName = userNameEditText.getText().toString();
                String password = passEditText.getText().toString();

                QBUser qbUser = new QBUser(userName, password);

                qbUser.setFullName(fullNameEditText.getText().toString());

                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(SignUpActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
