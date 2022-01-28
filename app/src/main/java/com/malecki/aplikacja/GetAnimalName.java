package com.malecki.aplikacja;

import static java.lang.System.currentTimeMillis;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.chaquo.python.Python;

import java.io.ByteArrayOutputStream;

public class GetAnimalName extends AsyncTask<Bitmap, String, Void>
{
    private TextView textView;
    private String nameOfAnimal;

    public GetAnimalName(TextView textView)
    {
        this.textView = textView;
    }

    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
            if(!isCancelled())
            {
                Python py = Python.getInstance();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                nameOfAnimal = py.getModule("Loading test")
                        .callAttr("recognise", byteArray).toString();
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
}
