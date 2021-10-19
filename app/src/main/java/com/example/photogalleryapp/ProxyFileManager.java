package com.example.photogalleryapp;

import android.app.Activity;
import android.net.Uri;

/** Proxy - Proxy FileManager */
public class ProxyFileManager implements IFileManager {
    IFileManager fileManager;

    ProxyFileManager() {
        fileManager = new FileManager();
    }

    public void saveFile(Uri uri, Activity view) {
        fileManager.saveFile(uri, view);
    }
}