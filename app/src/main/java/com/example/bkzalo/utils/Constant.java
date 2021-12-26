/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:16 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 12/21/21, 7:10 PM
 * /
 */

package com.example.bkzalo.utils;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class Constant {
    public static int UID=0;
    public static String PHONE="";
    public static GoogleSignInClient mGoogleSignInClient;
    public static String NAME="";
    public static String IMAGE="";
//    public static String SERVER_URL = "http://192.168.1.6/bkzalo/";
//    public static String SERVER_NODE = "http://192.168.1.6:3000/";
    public static String SERVER_URL = "http://192.168.1.111/bkzalo/";
    public static String SERVER_NODE = "http://192.168.1.111:3000/";
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
