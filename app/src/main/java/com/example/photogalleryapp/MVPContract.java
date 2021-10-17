package com.example.photogalleryapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public interface MVPContract {
    interface View {
        void displayPhoto(String path);
        void launchCamera(Uri photoUri);
    }

    interface Model {
        ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                     double minLongitude, double maxLongitude, double minLatitude, double maxLatitude);
        String updatePhoto(String path, String caption);
    }
    interface Presenter {
        void capture(Context context);
        File createImageFile(Context context, double latitude, double longitude) throws IOException;
        void onSuccessfulCapture();
        Intent search(Context context);
        void onSearchResult(Intent intent);
        void updatePhoto(String caption);
        void scrollPhotos(android.view.View v);
        void shareImage(Context context, Bitmap bitmap);
    }
}
