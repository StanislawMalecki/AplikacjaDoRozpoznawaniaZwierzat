package com.example.aplikacja;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG="MainActivity";

    private Handler threadHandler = new Handler();
    private Mat mRgba;
    private Mat frame;
    private Mat mGray;
    private Bitmap bitmap;
    private Switch detectionSwitch;
    private int frame_counter = 0;
    private Button goBackButton;
    private TextView whatAnimal;
    private boolean detectionOn = false;
    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface
                        .SUCCESS:
                {
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraActivity()
    {
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }


        detectionSwitch = findViewById(R.id.Animal_detection);
        whatAnimal = findViewById(R.id.whatAnimalCamera);
        detectionSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectionOn = detectionSwitch.isChecked();
            }
        });


        goBackButton=findViewById(R.id.goBack);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detectionOn)
                {
                    Toast.makeText(CameraActivity.this, "Najpierw wyłacz rozpoznawanie", Toast.LENGTH_SHORT);
                }
                else
                {
                    startActivity(new Intent(CameraActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (OpenCVLoader.initDebug())
        {
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else
            {
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView !=null)
        {
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(mOpenCvCameraView !=null)
        {
            mOpenCvCameraView.disableView();
        }
    }

    public void onCameraViewStarted(int width ,int height)
    {
        mRgba = new Mat(height, width, CvType.CV_64FC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
    }
    public void onCameraViewStopped()
    {
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        mRgba=inputFrame.rgba();
        if(detectionOn)
        {
            if(frame_counter % 30 ==0)
            {
                getAnimal(whatAnimal);
            }
            frame_counter++;
        }

        return mRgba;
    }

    public void getAnimal(TextView textView)
    {
        frame = mRgba;
        GetAnimalName animalName = new GetAnimalName(textView);
        Utils.matToBitmap(frame, bitmap);
        animalName.execute(bitmap);
    }
}