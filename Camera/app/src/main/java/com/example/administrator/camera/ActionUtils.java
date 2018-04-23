package com.example.administrator.camera;

import android.Manifest;

public class ActionUtils {
    public static final String ACTION_MY_IMAGE_CAPTURE = "android.media.action.MYIMAGE_CAPTURE";
    public static final String ACTION_MAINACTIVITY = "android.intent.action.MAIN";

    public static final int ACTION_BASE = 100;
    public static final int ACTION_SYSTEM_CAMERA_CALLBACK = ACTION_BASE +1;
    public static final int ACTION_SYSTEM_CAMERA_PATH_PICTURE = ACTION_BASE +2;


    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
}
