package com.example.aplikacja;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

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
    private Bitmap image;
    private TextView whatAnimal;
    ImageView i1;
    boolean isImageUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        animalName = new GetAnimalName(textView);
        image.reconfigure(image.getHeight(), image.getWidth(), Bitmap.Config.RGB_565);
        animalName.execute(image);
    }
}