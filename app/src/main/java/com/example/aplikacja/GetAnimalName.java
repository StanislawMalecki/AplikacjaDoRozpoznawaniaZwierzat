package com.example.aplikacja;

import static java.lang.System.currentTimeMillis;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.chaquo.python.Python;

import java.io.ByteArrayOutputStream;

public class GetAnimalName extends AsyncTask<Bitmap, String, Void>
{
    private TextView textView;
    private String nameOfAnimal;
    private Switch detectionSwitch = null;

    public GetAnimalName(TextView textView)
    {
        this.textView = textView;
    }

    public GetAnimalName(TextView textView, Switch detectionSwitch)
    {
        this.textView = textView;
        this.detectionSwitch = detectionSwitch;
    }

    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
            if(!isCancelled())
            {
                Long start = currentTimeMillis();

                Python py = Python.getInstance();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                nameOfAnimal = py.getModule("Loading test").callAttr("test", byteArray).toString();

                Long time = currentTimeMillis()-start;
                Log.i("czas", "_"+time);
                CameraActivity.allTimes.add(time);

                Handler threadHandler = new Handler(Looper.getMainLooper());
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(nameOfAnimal);
                    }
                });
            }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i("zdechł??_",isCancelled()+"");
    }
}
