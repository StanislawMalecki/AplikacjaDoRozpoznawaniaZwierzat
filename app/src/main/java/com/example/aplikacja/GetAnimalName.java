package com.example.aplikacja;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.chaquo.python.Python;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

public class GetAnimalName extends AsyncTask<Bitmap, String, String>
{
    private TextView textView;
    private String nameOfAnimal;

    public GetAnimalName(TextView textView)
    {
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        Python py = Python.getInstance();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        nameOfAnimal = py.getModule("Loading test").callAttr("test", byteArray).toString();
        return nameOfAnimal;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Handler threadHandler = new Handler(Looper.getMainLooper());
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(nameOfAnimal);
            }
        });
    }
}
