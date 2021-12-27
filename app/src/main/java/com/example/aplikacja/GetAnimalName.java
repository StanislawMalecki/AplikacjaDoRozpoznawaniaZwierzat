package com.example.aplikacja;

import android.graphics.Bitmap;

import com.chaquo.python.Python;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

public class GetAnimalName implements Runnable
{
    private static Mat FRAME = new Mat();
    private static Bitmap bmp;
    private static Bitmap bitmap;
    boolean gotInMat;
    private String nameOfAnimal;

    public GetAnimalName(Mat frame)
    {
        gotInMat =true;
        FRAME = frame;
    }

    public GetAnimalName(Bitmap newBmp) {
        gotInMat = false;
        bmp=newBmp;
    }

    @Override
    public void run() {
        classifyAnimal();
    }

    public void classifyAnimal()
    {
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
    }

    public String getNameOfAnimal()
    {
        return nameOfAnimal;
    }

}
