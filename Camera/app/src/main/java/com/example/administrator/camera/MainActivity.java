package com.example.administrator.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private ImageView mImageView;

    private String mSystemPicturePath;

    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
    }

    private void init() {
        mImageView = findViewById(R.id.camera_imageview);

        getSystemPicture();

    }

    private void getSystemPicture() {
        mSystemPicturePath = Environment.getExternalStorageDirectory().getPath();
        mSystemPicturePath = mSystemPicturePath + "/"+"temp.png";
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //系统的camera
        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,ActionUtils.ACTION_SYSTEM_CAMERA_CALLBACK);
        //自定义的camera
        } else if (id == R.id.nav_mycamera) {
            Intent intent = new Intent(ActionUtils.ACTION_MY_IMAGE_CAPTURE);
            startActivity(intent);
            //系统的camera
        } else if (id == R.id.nav_systempathpool) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = Uri.fromFile(new File(mSystemPicturePath));
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent,ActionUtils.ACTION_SYSTEM_CAMERA_PATH_PICTURE);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case ActionUtils.ACTION_SYSTEM_CAMERA_CALLBACK:
                    Bundle bundle = data.getExtras();
                    if (bundle != null){
                        mBitmap = (Bitmap) bundle.get("data");
                        mImageView.setImageBitmap(mBitmap);
                    }
                    break;
                case ActionUtils.ACTION_SYSTEM_CAMERA_PATH_PICTURE:
                    Log.d(TAG, "laiyu: ");
                    FileInputStream fis =null;
                    //保证etc/storage/有访问的permission
                    try {
                        fis = new FileInputStream(mSystemPicturePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    mBitmap = BitmapFactory.decodeStream(fis);
                    Log.d(TAG, "mBitmap =  "+fis);
                    mImageView.setImageBitmap(mBitmap);
                    if (fis != null){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == ActionUtils.ACTION_SYSTEM_CAMERA_CALLBACK) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                mImageView.setImageBitmap(bitmap);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == ActionUtils.ACTION_SYSTEM_CAMERA_PATH_PICTURE) {
                try {
                    FileInputStream fis = new FileInputStream(mSystemPicturePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    mImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "onActivityResult FileNotFoundException ");
                }
            }
        }
    }
}
