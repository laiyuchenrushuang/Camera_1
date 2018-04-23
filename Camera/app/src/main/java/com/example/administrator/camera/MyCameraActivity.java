package com.example.administrator.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "MyCameraActivity" ;

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private Button mButton;
    private SurfaceHolder mHolder;

    private Button mTakePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycamera);
        //camera的权限 还有 sdcard读写 增删权限
        permision();

        init();
    }

    private void permision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST","Granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);//1 can be another integer
        }

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, ActionUtils.PERMISSIONS_STORAGE,
                    ActionUtils.REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void init() {
        mSurfaceView = findViewById(R.id.camera_surface);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mTakePicture = findViewById(R.id.camera_take_picture);
        mTakePicture.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        Log.d(TAG, "camera: " +camera);
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private Camera getCamera() {
        Camera camera ;
        try {
            camera= Camera.open();
        }catch (Exception e){
            Log.d(TAG, "getCamera Exception" );
            camera =null;
        }
        Log.d(TAG, "camera1 " +camera);
        return camera;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureDir = new File("/sdcard/temp.png");
            if (pictureDir == null) {
                Log.d(TAG,
                        "Error creating media file, check storage permissions!");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureDir);
                fos.write(data);
                fos.close();

                Intent i = new Intent(MyCameraActivity.this,MainActivity.class);
                i.putExtra("pathPicture",pictureDir.getPath());
                Log.d(TAG, "onPictureTaken: ");
                startActivity(i);
                MyCameraActivity.this.finish();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.camera_take_picture:
                capture(view);
        }
    }

    private void capture(View view) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(800,400);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success){
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        });
    }
}
