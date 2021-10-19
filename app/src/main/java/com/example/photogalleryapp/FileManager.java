package com.example.photogalleryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/** Proxy - Service FileManager */
public class FileManager implements IFileManager {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public void saveFile(Uri photoURI, Activity view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(view.getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            view.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }
}