package com.example.aplikacja;

import static java.lang.System.currentTimeMillis;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.chaquo.python.Python;

import java.io.ByteArrayOutputStream;

public class GetAnimalName extends AsyncTask<Bitmap, String, Void>
{
    private TextView textView;
    private String nameOfAnimal;
    private Switch detectionSwitch = null;
    public boolean off = false;

    public GetAnimalName(TextView textView)
    {
        this.textView = textView;
        off = false;
    }

    public GetAnimalName(TextView textView, Switch detectionSwitch)
    {
        off = false;
        this.textView = textView;
        this.detectionSwitch = detectionSwitch;
    }

    public boolean isOff() {
        return off;
    }

    public void setOff(boolean canceled) {
        this.off = canceled;
    }

    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
            while(!isOff())
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
                        if(detectionSwitch!=null && !detectionSwitch.isChecked())
                        {
                            setOff(true);
                        }

                    }
                });
            }

        return null;
    }
}
