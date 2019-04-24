package com.example.quickbloxchat;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.example.quickbloxchat.model.SampleConfigs;
import com.quickblox.sample.core.CoreApp;
import com.quickblox.sample.core.utils.ActivityLifecycle;

import java.util.Locale;

public class App extends CoreApp {

    private static final String TAG = App.class.getSimpleName();
    private static SampleConfigs sampleConfigs;
    //private static CommonInstances commonInstances;
    private static App instance;

    public static App getContext() {
        return instance;
    }

    //public static CommonInstances getCommonInstances() {
//        return commonInstances;
//    }

    public static SampleConfigs getSampleConfigs() {
        return sampleConfigs;
    }

    @Override
    public void onCreate() {
        super.onCreate();

       // commonInstances = new CommonInstances();
        ActivityLifecycle.init(this);
        //initSampleConfigs();


    }

//    private void initSampleConfigs() {
//        try {
//            sampleConfigs = ConfigUtils.getSampleConfigs(Consts.SAMPLE_CONFIG_FILE_NAME);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // setting locale for the whole application
    public void setResourceLocale(Locale locale) {
        if (Build.VERSION.SDK_INT >= 17) {
            getBaseContext().getResources().getConfiguration().setLocale(locale);
        } else {
            Configuration config = getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    //for setting locale on the basis of language selection
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}