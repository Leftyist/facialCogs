package com.arianfarahani.facialcogs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CaptureResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainLoginActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    final                int    REQUEST_USE_CAMERA = 0;
    CameraHelper cameraHelper;
    AtomicBoolean cameraPermission = new AtomicBoolean(false);
    AtomicBoolean verifying = new AtomicBoolean(false);
    RelativeLayout spinner;

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        cameraHelper = new CameraHelper();
        spinner = findViewById(R.id.relativelayout_progress);

        checkPermissions(); //also starts the preview

        Button capButton = (Button) findViewById(R.id.button_capture);
        capButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (cameraPermission.get())
                {
                    cameraHelper.takeSnapshot();
                }
            }
        });
    }

    private VerifyTask.VerifyResults verificationListener = new VerifyTask.VerifyResults()
    {
        @Override
        public void onReturn(boolean verified)
        {
            spinner.setVisibility(View.GONE);
            verifying.set(false);
            
            if (verified)
            {
                Intent intent = new Intent(spinner.getContext(), HomeActivity.class);
                spinner.getContext().startActivity(intent);
            } else
            {
                cameraHelper.startPreview();
                Toast.makeText(spinner.getContext(), "Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStart()
        {
            spinner.setVisibility(View.VISIBLE);
        }
    };

    private CameraHelper.OnCameraRequest resultListener = new CameraHelper.OnCameraRequest()
    {
        @Override
        public void snapshotResult(CaptureResult result)
        {
            RelativeLayout spinner = findViewById(R.id.relativelayout_progress);
            if(!verifying.get()) {
                verifying.set(true);
                new VerifyTask(verificationListener).execute(result);
            }

        }
    };

    private static class VerifyTask extends AsyncTask<CaptureResult, Integer, Boolean>
    {
        private VerifyResults listener;

        interface VerifyResults
        {
            public void onReturn(boolean verified);

            public void onStart();
        }

        public VerifyTask(VerifyResults listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            listener.onStart();
        }

        @Override
        protected Boolean doInBackground(CaptureResult... captureResults)
        {
            try
            {
                Thread.sleep(2000);
            } catch (Exception e)
            {

            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean verified)
        {
            super.onPostExecute(verified);
            listener.onReturn(verified);
        }

    }

    private void fillCameraHelper()
    {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        cameraHelper.attachView(this, surfaceView, resultListener);
        cameraPermission.set(true);
    }

    private void checkPermissions()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.CAMERA},
                                              REQUEST_USE_CAMERA
            );
        } else
        {
            fillCameraHelper();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_USE_CAMERA:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    fillCameraHelper();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else
                {
                    Toast.makeText(this, "Permissions not granted :(", Toast.LENGTH_SHORT).show();
                    cameraPermission.set(false);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}

