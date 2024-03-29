package com.malecki.aplikacja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.opencv.android.OpenCVLoader;

public class  MainActivity extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }
    private GetAnimalName animalName = new GetAnimalName(null);
    private Button camera_button;
    private Button animal_recognition;
    private ImageButton help;
    private Bitmap image;
    public static boolean firstrun;
    private TextView whatAnimal;
    ImageView i1;
    boolean isImageUploaded = false;
    private int helpEnum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isImageUploaded = false;

        OpenCVLoader.initDebug();

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        int MY_PERMISSIONS_REQUEST_STORAGE=0;
        // if storage permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }

        i1 = findViewById(R.id.imageView);
        camera_button=findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animalName.cancel(true);
                startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        whatAnimal = findViewById(R.id.whatAnimalMain);
        animal_recognition=findViewById(R.id.animal_recognition);
        animal_recognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageUploaded)
                {
                    getAnimal(whatAnimal);
                }
                else
                {
                    whatAnimal.setText("Najpierw wybierz obraz");
                }
            }
        });

        help = findViewById(R.id.imageButton);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpEnum=0;
                firstrun=true;
                runHelp();
            }
        });
        firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun)
        {
            helpEnum = 0;
            runHelp();
        }
    }

    public void openGallery (View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==101 && resultCode == RESULT_OK && data!=null)
        {
            Uri imageUri = data.getData();

            String path = getPath(imageUri);

            Bitmap oldPic = BitmapFactory.decodeFile(path);
            Bitmap newPic = Bitmap.createScaledBitmap(oldPic, i1.getWidth(), i1.getHeight(), true);
            image = newPic;
            i1.setImageBitmap(newPic);
            isImageUploaded = true;
        }
    }

    private String getPath(Uri uri) {
        if(uri==null)
        {
            return null;
        }
        else
        {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
            if(cursor != null)
            {
                int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(col_index);
            }
        }

        return uri.getPath();
    }

    public void getAnimal(TextView textView)
    {
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(MainActivity.this));
        }
        Python py = Python.getInstance();
        py.getModule("Loading test").callAttr("prepare");

        animalName = new GetAnimalName(textView);
        image.reconfigure(image.getHeight(), image.getWidth(), Bitmap.Config.RGB_565);
        animalName.execute(image);
    }

    public void runHelp()
    {
        ViewTarget camera = new ViewTarget(R.id.camera_button, this);
        ViewTarget animalRec = new ViewTarget(R.id.animal_recognition, this);
        ViewTarget image = new ViewTarget(R.id.loadImage, this);
        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .setTarget(camera)
                .setContentTitle(HelpInstructionEnum.CONTENT_TITLE_CAMERA_VIEW_BUTTON.text)
                .setContentText(HelpInstructionEnum.CONTENT_TEXT_CAMERA_VIEW_BUTTON.text)
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
                        showcaseView.setTarget(image);
                        showcaseView.setContentText(HelpInstructionEnum.CONTENT_TEXT_LOAD_IMAGE_BUTTON.text);
                        showcaseView.setContentTitle(HelpInstructionEnum.CONTENT_TITLE_LOAD_IMAGE_BUTTON.text);

                        break;
                    case 2:
                        showcaseView.setTarget(animalRec);
                        showcaseView.setContentText(HelpInstructionEnum.CONTENT_TEXT_ANIMAL_RECOGNITION_BUTTON.text);
                        showcaseView.setContentTitle(HelpInstructionEnum.CONTENT_TITLE_ANIMAL_RECOGNITION_BUTTON.text);
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