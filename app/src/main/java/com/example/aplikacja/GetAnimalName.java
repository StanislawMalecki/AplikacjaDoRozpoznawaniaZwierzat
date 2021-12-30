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

public class GetAnimalName extends AsyncTask<Mat, String, String>
{
    private static Mat FRAME = new Mat();
    private static Bitmap bmp;
    private static Bitmap bitmap;
    boolean gotInMat;
    TextView textView;
    private String nameOfAnimal = "processing";

    public GetAnimalName(Mat frame, TextView textView)
    {
        gotInMat =true;
        FRAME = frame;
        this.textView = textView;
    }

    public GetAnimalName(Bitmap newBmp, TextView textView) {
        gotInMat = false;
        bmp=newBmp;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Mat... mats) {
        Python py = Python.getInstance();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(gotInMat)
        {
            bitmap = Bitmap.createBitmap(FRAME.cols(),FRAME.rows(),Bitmap.Config.RGB_565);
            Utils.matToBitmap(FRAME, bitmap);
        }
        else
        {
            bitmap = bmp;
            bitmap.reconfigure(bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.RGB_565);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Handler threadHandler = new Handler(Looper.getMainLooper());
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText("Przetwarzanie...");
            }
        });

    }
}
