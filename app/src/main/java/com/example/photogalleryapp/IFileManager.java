package com.example.photogalleryapp;

import android.app.Activity;
import android.net.Uri;

/** Proxy - Interface FileManager */
public interface IFileManager {
    void saveFile(Uri uri, Activity view);
}