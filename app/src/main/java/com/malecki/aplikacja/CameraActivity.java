package com.malecki.aplikacja;

import static com.malecki.aplikacja.MainActivity.firstrun;

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
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG="MainActivity";

    private Handler threadHandler = new Handler();
    private Mat mRgba;
    private Mat frame;
    private Bitmap bitmap;
    private Switch detectionSwitch;
    private Button goBackButton;
    private ImageButton help;
    private TextView whatAnimal;
    GetAnimalName animalName = new GetAnimalName(null);
    private boolean detectionOn = false;
    private CameraBridgeViewBase mOpenCvCameraView;
    private int helpEnum;
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
        helpEnum = 0;

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

        detectionSwitch = findViewById(R.id.Animal_detection);
        whatAnimal = findViewById(R.id.whatAnimalCamera);
        detectionOn = false;
        detectionSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectionOn = detectionSwitch.isChecked();
                if(!detectionSwitch.isChecked())
                {
                    animalName.cancel(true);
                }
            }
        });

        goBackButton=findViewById(R.id.goBack);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectionSwitch.setChecked(false);
                detectionOn = false;
                animalName.cancel(true);
                startActivity(new Intent(CameraActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        help= findViewById(R.id.imageButtonCamera);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runHelp();
            }
        });

        if (firstrun)
        {
        helpEnum = 0;
        runHelp();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .edit()
                .putBoolean("firstrun", false)
                .commit();

        }

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(CameraActivity.this));
        }
        Python py = Python.getInstance();
        py.getModule("Loading test").callAttr("prepare");
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
        if(!animalName.isCancelled())
        {
            animalName.cancel(true);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(mOpenCvCameraView !=null)
        {
            mOpenCvCameraView.disableView();
        }
        if(!animalName.isCancelled())
        {
            animalName.cancel(true);
        }
    }

    public void onCameraViewStarted(int width ,int height)
    {
        mRgba = new Mat(height, width, CvType.CV_64FC4);
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
            getAnimal(whatAnimal, detectionSwitch);
        }
        return mRgba;
    }

    public void getAnimal(TextView textView, Switch detectionSwitch)
    {
        frame = mRgba;
        animalName = new GetAnimalName(textView);
        Utils.matToBitmap(frame, bitmap);
        animalName.execute(bitmap);
    }

    public void runHelp()
    {
        ViewTarget goBack = new ViewTarget(R.id.goBack, this);
        ViewTarget animalDet = new ViewTarget(R.id.Animal_detection, this);
        ViewTarget camera = new ViewTarget(R.id.frame_Surface, this);
        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .setTarget(goBack)
                .setContentTitle(HelpInstructionEnum.CONTENT_TITLE_GO_BACK_BUTTON.text)
                .setContentText(HelpInstructionEnum.CONTENT_TEXT_GO_BACK_BUTTON.text)
                .setStyle(R.style.CustomShowcaseTheme1)
                .build();
        showcaseView.setButtonText("Następny");
        showcaseView.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpEnum++;
                switch (helpEnum)
                {
                    case 1:
                        showcaseView.setTarget(animalDet);
                        showcaseView.setContentText(HelpInstructionEnum.CONTENT_TEXT_ANIMAL_RECOGNITION_SWITCH.text);
                        showcaseView.setContentTitle(HelpInstructionEnum.CONTENT_TITLE_ANIMAL_RECOGNITION_SWITCH.text);
                        break;
                    case 2:
                        showcaseView.setTarget(camera);
                        showcaseView.setContentText(HelpInstructionEnum.CONTENT_TEXT_CAMERA_VIEW.text);
                        showcaseView.setContentTitle(HelpInstructionEnum.CONTENT_TITLE_CAMERA_VIEW.text);
                        showcaseView.setHideOnTouchOutside(true);
                        break;
                    case 3:
                        showcaseView.hide();
                        break;
                }

            }
        });
    }
}