package com.arianfarahani.facialcogs;

import android.content.Context;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraHelper
{
    final static String TAG = "CameraHelper";
    private SurfaceView surfaceView;
    private Context context;
    private AtomicBoolean preview = new AtomicBoolean(false);
    private OnCameraRequest resultListener = null;

    interface OnCameraRequest {

        void snapshotResult(CaptureResult result);
    }

    public void attachView(Context cntxt, SurfaceView sv, OnCameraRequest listener) {
        surfaceView = sv;
        context = cntxt;
        resultListener = listener;
        surfaceView.getHolder().addCallback(holderCallback);
    }

    private SurfaceHolder.Callback holderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder)
        {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
        {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder)
        {

        }
    };

    private CameraDevice.StateCallback cameraPreviewInterface = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice)
        {
            SurfaceHolder holder = surfaceView.getHolder();
            List<Surface> list   = new ArrayList<>();
            list.add(holder.getSurface());
            try
            {
                cameraDevice.createCaptureSession(list, capturePreviewInterface, null);
            } catch (Exception e)
            {
                Log.d(TAG, e.toString());
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice)
        {
            Log.d(TAG, "Camera Disconnected.");
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i)
        {
            Log.e(TAG, "Camera Error.");
        }
    };

    private CameraCaptureSession.StateCallback capturePreviewInterface = new CameraCaptureSession.StateCallback()
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
                                                                     .createCaptureRequest(
                                                                             CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surfaceView.getHolder().getSurface());

                cameraCaptureSession.setRepeatingRequest(builder.build(),
                                                         capturePreviewCallback, null);
            } catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

        }
    };

    private CameraCaptureSession.CaptureCallback capturePreviewCallback
            = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureProgressed(
                @NonNull CameraCaptureSession session,
                @NonNull CaptureRequest request,
                @NonNull CaptureResult partialResult)
        {
            super.onCaptureProgressed(session, request, partialResult);
            if(!preview.get()) {
                try{
                    session.stopRepeating();
                    resultListener.snapshotResult(partialResult);
                    //takeSingle();
                } catch(Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

        }
    };

    public void startPreview()
    {
        CameraManager cameraManager = context.getSystemService(CameraManager.class);
        preview.set(true);

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
                    cameraManager.openCamera(camera, cameraPreviewInterface, null);
                }
            }
            // attempt to get a Camera instance
        } catch (SecurityException e)
        {
            Toast.makeText(context, "No camera found :(", Toast.LENGTH_SHORT).show();
        } catch (Exception ex)
        {
            Toast.makeText(context, "No camera found :(", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    private void takeSingle() {
        CameraManager cameraManager = context.getSystemService(CameraManager.class);

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
                    cameraManager.openCamera(camera, cameraSingleInterface, null);
                }
            }
            // attempt to get a Camera instance
        } catch (SecurityException e)
        {
            Toast.makeText(context, "No camera found :(", Toast.LENGTH_SHORT).show();
        } catch (Exception ex)
        {
            Toast.makeText(context, "No camera found :(", Toast.LENGTH_SHORT).show();
        }
    }

    private CameraDevice.StateCallback cameraSingleInterface = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice)
        {
            SurfaceHolder holder = surfaceView.getHolder();
            List<Surface> list   = new ArrayList<>();
            list.add(holder.getSurface());
            try
            {
                cameraDevice.createCaptureSession(list, captureSingleInterface, null);
            } catch (Exception e)
            {
                Log.d(TAG, e.toString());
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice)
        {
            Log.d(TAG, "Camera Disconnected.");
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i)
        {
            Log.e(TAG, "Camera Error.");
        }
    };

    private CameraCaptureSession.StateCallback captureSingleInterface = new CameraCaptureSession.StateCallback()
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
                                                                     .createCaptureRequest(
                                                                             CameraDevice.TEMPLATE_STILL_CAPTURE);

                builder.addTarget(surfaceView.getHolder().getSurface());

                cameraCaptureSession.setRepeatingRequest(builder.build(),
                                                         captureSingleCallback, null);
            } catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

        }
    };

    private CameraCaptureSession.CaptureCallback captureSingleCallback
            = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureCompleted(
                @NonNull CameraCaptureSession session,
                @NonNull CaptureRequest request,
                @NonNull TotalCaptureResult result)
        {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(context, "Capture Complete", Toast.LENGTH_SHORT).show();
        }
    };
    */


    public void takeSnapshot() {
        preview.set(false);
    }



}
