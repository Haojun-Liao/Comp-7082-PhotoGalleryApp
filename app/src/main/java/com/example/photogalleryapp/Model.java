package com.example.photogalleryapp;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Model implements MVPContract.Model{

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                        double minLongitude, double maxLongitude, double minLatitude, double maxLatitude) {
//    ){
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            Arrays.stream(fList).forEach((f) -> {
                String attr[] = f.getPath().split("_");
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords == "" || f.getPath().contains(keywords))
                        && ((minLongitude == 0.0 && maxLongitude == 0.0) || (Double.parseDouble(attr[3]) >= minLongitude
                        && Double.parseDouble(attr[3]) <= maxLongitude))
                        && ((minLatitude == 0.0 && maxLatitude == 0.0) || (Double.parseDouble(attr[4]) >= minLatitude
                        && Double.parseDouble(attr[4]) <= maxLatitude))
                )
                    photos.add(f.getPath());
            });
//            for (File f : fList) {
//                String attr[] = f.getPath().split("_");
//                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
//                        && f.lastModified() <= endTimestamp.getTime())
//                ) && (keywords == "" || f.getPath().contains(keywords))
//                        && ((minLongitude == 0.0 && maxLongitude == 0.0) || (Double.parseDouble(attr[3]) >= minLongitude
//                        && Double.parseDouble(attr[3]) <= maxLongitude))
//                        && ((minLatitude == 0.0 && maxLatitude == 0.0) || (Double.parseDouble(attr[4]) >= minLatitude
//                        && Double.parseDouble(attr[4]) <= maxLatitude))
//                )
//                    photos.add(f.getPath());
//            }
        }
        return photos;
    }
    @Override
    public String updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 5) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5]);
            File from = new File(path);
            from.renameTo(to);
            return to.getAbsolutePath();
        }
        return "";
    }





}
