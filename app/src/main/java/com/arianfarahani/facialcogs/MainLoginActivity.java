package com.arianfarahani.facialcogs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class MainLoginActivity extends AppCompatActivity
{
    private static final String TAG                = "MainActivity";
    final                int    REQUEST_USE_CAMERA = 0;
    SurfaceView surfaceView;

    CameraDevice.StateCallback cameraInterface = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice)
        {
            surfaceView     = (SurfaceView) findViewById(R.id.surfaceView);
            SurfaceHolder holder = surfaceView.getHolder();
            List<Surface> list   = new ArrayList<Surface>();
            list.add(holder.getSurface());
            try
            {
                cameraDevice.createCaptureSession(list, captureInterface, null);
            } catch (Exception e)
            {
                Log.d(TAG, e.toString());
                Toast.makeText(MainLoginActivity.this,
                               "Couldn't capture session.",
                               Toast.LENGTH_SHORT
                )
                     .show();
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice)
        {
            Toast.makeText(MainLoginActivity.this, "Camera Disconnected.", Toast.LENGTH_SHORT)
                 .show();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i)
        {
            Toast.makeText(MainLoginActivity.this, "Camera Error.", Toast.LENGTH_SHORT).show();
        }
    };

    CameraCaptureSession.StateCallback captureInterface = new CameraCaptureSession.StateCallback()
    {
        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession)
        {
            Log.e(TAG, "Config Failed");
        }

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "Config Success");
            try
            {
                CaptureRequest.Builder builder = cameraCaptureSession.getDevice()
                                                        .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surfaceView.getHolder().getSurface());

                cameraCaptureSession.setRepeatingRequest(builder.build(), captureCallback, null);
            } catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

        }
    };

    CameraCaptureSession.CaptureCallback captureCallback
            = new CameraCaptureSession.CaptureCallback()
    {


    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        Button capButton = (Button) findViewById(R.id.button_capture);
        capButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkPermissions();
            }
        });

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
            getCameraInstance();
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
                    getCameraInstance();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else
                {
                    Toast.makeText(this, "Permissions not granted :(", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public void getCameraInstance()
    {
        CameraManager cameraManager = this.getSystemService(CameraManager.class);

        try
        {
            String[] cameras = cameraManager.getCameraIdList();
            for (String camera : cameras)
            {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(
                        camera);
                if (characteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT)
                {
                    cameraManager.openCamera(camera, cameraInterface, null);
                }
            }
            // attempt to get a Camera instance
        } catch (SecurityException e)
        {
            Toast.makeText(this, "No camera found :(", Toast.LENGTH_SHORT).show();
        } catch (Exception ex)
        {
            Toast.makeText(this, "No camera found :(", Toast.LENGTH_SHORT).show();
        }
        return; // returns null if camera is unavailable
    }
}

