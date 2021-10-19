package com.example.photogalleryapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Presenter implements MVPContract.Presenter {
    private static Presenter single_instance = null;

    public void setMainView(MVPContract.View mainView) {
        this.mainView = mainView;
    }

    public void setModel(MVPContract.Model model) {
        this.model = model;
    }

    private MVPContract.View mainView;
    private MVPContract.Model model;
    private int index;
    private String mCurrentPhotoPath;
    private ArrayList<String> photos;

    private Presenter(MVPContract.View mainView, MVPContract.Model model) {
        this.mainView = mainView;
        this.model = model;
        this.index = 0;
        mCurrentPhotoPath = null;
        photos = model.findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0, 0, 0, 0);
        displayPhoto();
    }

    public static Presenter getInstance(MVPContract.View mainView, MVPContract.Model model) {
        if(single_instance == null){
            single_instance = new Presenter(mainView, model);
        }
        return single_instance;
    }

    public static Presenter getInstance() {
        return single_instance;
    }

    @Override
    public void capture(Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                File photoFile = null;
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            photoFile = createImageFile(context, location.getLatitude(), location.getLongitude());
                            Uri photoUri = FileProvider.getUriForFile(context, "com.example.photogalleryapp.fileprovider", photoFile);
                            mainView.launchCamera(photoUri);
                            mainView.displayPhoto(photoFile.getAbsolutePath());
                        } catch (IOException e) {

                        }

                    }
                }
            });
        }
    }

    @Override
    public File createImageFile(Context context, double latitude, double longitude) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "caption_" + timeStamp + "_" + latitude + "_" + longitude + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onSuccessfulCapture() {
        photos.add(mCurrentPhotoPath);
        index = photos.size() - 1;
        mainView.displayPhoto(mCurrentPhotoPath);
    }

    @Override
    public Intent search(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Override
    public void onSearchResult(Intent data) {
        DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        Date startTimestamp, endTimestamp;
        double minLongitude, maxLongitude, minLatitude, maxLatitude;
        try {
            String from = (String) data.getStringExtra("STARTTIMESTAMP");
            String to = (String) data.getStringExtra("ENDTIMESTAMP");
            startTimestamp = format.parse(from);
            endTimestamp = format.parse(to);
        } catch (Exception ex) {
            startTimestamp = null;
            endTimestamp = null;
        }
        String keywords = (String) data.getStringExtra("KEYWORDS");

        try {
            minLongitude = Double.parseDouble(data.getStringExtra("MINLONGITUDE"));
            maxLongitude = Double.parseDouble(data.getStringExtra("MAXLONGITUDE"));
            minLatitude = Double.parseDouble(data.getStringExtra("MINLATITUDE"));
            maxLatitude = Double.parseDouble(data.getStringExtra("MAXLATITUDE"));

        } catch (Exception e) {
            minLongitude = 0;
            maxLongitude = 0;
            minLatitude = 0;
            maxLatitude = 0;
        }
        index = 0;
        photos = model.findPhotos(startTimestamp, endTimestamp, keywords,
                minLongitude, maxLongitude, minLatitude, maxLatitude);
        displayPhoto();
    }
    private void displayPhoto() {
        if (photos.size() == 0) {
            mainView.displayPhoto(null);
        } else {
            mainView.displayPhoto(photos.get(index));
        }
    }

    public void scrollPhotos(View v) {
        if (photos.size() == 0) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;
            default:
                break;
        }
//        Log.e("current index:", String.valueOf(index));
        mainView.displayPhoto(photos.get(index));
    }

    public void updatePhoto(String caption) {
        if (photos == null) {
            return;
        }
        String path = photos.get(index);
        String[] attr = path.split("_");
        if (attr.length >= 5) {
            photos.set(index, model.updatePhoto(path, caption));
        }
    }

    /** Share image to other apps */
    public void shareImage(Context context, Bitmap bitmap) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        /** Name of save image file */
        File f = new File(context.getExternalCacheDir() + "/" + context.getResources().getString(R.string.app_name));

        Intent shareIntent;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareIntent.setType("image/*");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /** Show the Sharesheet */
        context.startActivity(Intent.createChooser(shareIntent, "share image"));
    }




}
