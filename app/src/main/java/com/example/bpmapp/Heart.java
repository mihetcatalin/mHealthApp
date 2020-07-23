package com.example.bpmapp;


import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/*This activity takes advantage of the camera to make heart rate readings. When this activity is accessed it turns on both the camera and the flashlight
in torch mode. The camera offers a preview which then converted to red using the ImageProcessing class.
It compares the red average values with a new preview to determine if a beat was detected. The preview is displayed on the screen
 usign a surfaceView which updates constantly - being usually used for video streams*/

public class Heart extends AppCompatActivity {

    private static final String TAG = "";

    private Camera camera;
    private SurfaceView preview;
    private SurfaceHolder previewHolder;
    private static TextView text;

    private static int averageIndex = 0;
    private static int averageArraySize = 4;
    private static int[] valueArray = new int[averageArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static boolean beatDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        text = findViewById(R.id.showBPM);
        preview = findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null)
                throw new NullPointerException();

            Camera.Size size = cam.getParameters().getPreviewSize();

            if (size == null)
                throw new NullPointerException();

            int width = size.width;
            int height = size.height;
            int redAverage = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);

            int valueArrayAvg = 0;
            int valueArrayCnt = 0;
            for (int i = 0; i < valueArray.length; i++) {
                if (valueArray[i] > 0) {
                    valueArrayAvg += valueArray[i];
                    valueArrayCnt++;
                }
                System.out.println("index "+valueArrayCnt+": "+valueArrayAvg);
            }
            int rollingAverage = (valueArrayCnt > 0) ? (valueArrayAvg / valueArrayCnt) : 0;


            boolean previousBeat = beatDetected;
            if (redAverage < rollingAverage) {
                System.out.println("rollingAverage = "+valueArrayAvg+"/"+valueArrayCnt+"="+rollingAverage);
                System.out.println(redAverage+" < "+ rollingAverage);
                previousBeat = true;
                if (previousBeat != beatDetected) {
                    beats++;
                    System.out.println("beats: "+beats);
                }
            } else if (redAverage > rollingAverage) {
                previousBeat = false;
            }

            if (averageIndex == averageArraySize)
                averageIndex = 0;
            valueArray[averageIndex] = redAverage;
            averageIndex++;
            //here averageArray gets the value from redAverage to be used the next time
            // Transitioned from one state to another to the same
            if (previousBeat != beatDetected) {
                beatDetected = previousBeat;
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);

                int bpm = (int) (bps * 60d);
                System.out.println("beats/min: "+bpm);
                if (bpm < 30 || bpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    return;
                }

                text.setText(String.valueOf(bpm));
                startTime = System.currentTimeMillis();
                beats = 0;
            }


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setDisplayOrientation(90);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            for(int i: parameters.getSupportedPreviewFormats()) {
                Log.e(TAG, "preview format supported are = "+i);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

}


