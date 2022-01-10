package com.example.bkzalo.activitiy;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

//    private SharedPref sharedPref;

    @Override
    public void onCreate() {


        super.onCreate();

        //FirebaseApp.initializeApp(this);



//        sharedPref = new SharedPref(this);
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });

//        if(sharedPref.isDarkMode()){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

    }
}
