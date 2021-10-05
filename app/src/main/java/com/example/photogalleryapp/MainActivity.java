package com.example.photogalleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<String> photos = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int index = 0;
    String mCurrentPhotoPath;
    ImageView imageView;
    Button snapButton;

    double[] locationPoint = {0.0, 0.0};
    ;
    TextView longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        imageView = findViewById(R.id.imageView);
        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0, 0, 0, 0);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        snapButton = findViewById(R.id.snapButton);

        locationPermissionCheck();

        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationPermissionCheck();
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            File photoFile = null;
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (location != null) {
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    locationPoint[0] = location.getLongitude();
                                    locationPoint[1] = location.getLatitude();
                                    Log.d("00", String.valueOf(locationPoint[0]));
                                    Log.d("10", String.valueOf(locationPoint[1]));
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {

                                    }
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.photogalleryapp.fileprovider", photoFile);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                                    }
                                }
                            } else {
                                longitude.setText("longitude: unknown");
                                latitude.setText("latitude: unknown");
                                Log.d("location permission", "not granted");
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {

                                }
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.photogalleryapp.fileprovider", photoFile);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                                }

                            }
                        }
                        // Continue only if the File was successfully created

                    });
                }

            }
        });
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }


    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                         double minLongitude, double maxLongitude, double minLatitude, double maxLatitude) {
//    ){
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        Log.e("minLongitude:", String.valueOf(minLongitude));
        Log.e("maxLongitude:", String.valueOf(maxLongitude));
        Log.e("minLa:", String.valueOf(minLatitude));
        Log.e("MaxLa:", String.valueOf(maxLatitude));
        if (fList != null) {
            for (File f : fList) {
                String attr[] = f.getPath().split("_");
                Log.e("file0:", attr[0]);
                Log.e("file1:", attr[1]);
                Log.e("file2:", attr[2]);
                Log.e("file3:", attr[3]);
                Log.e("file4:", attr[4]);
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords == "" || f.getPath().contains(keywords))
                        && ((minLongitude == 0.0 && maxLongitude == 0.0) || (Double.parseDouble(attr[3]) >= minLongitude
                        && Double.parseDouble(attr[3]) <= maxLongitude))
                        && ((minLatitude == 0.0 && maxLatitude == 0.0) || (Double.parseDouble(attr[4]) >= minLatitude
                        && Double.parseDouble(attr[4]) <= maxLatitude))
                )
                    photos.add(f.getPath());
            }
        }
        return photos;
    }

    public void search(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 2);
    }

    public void scrollPhotos(View v) {
        if (photos.size() == 0) {
            return;
        }
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
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
        displayPhoto(photos.get(index));
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path == "") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 5) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5] + "_");
            File from = new File(path);
            from.renameTo(to);
            photos.set(index, attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5] + "_");
        }

//        String[] attr = path.split("_");
//        if (attr.length >= 3) {
//            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
//            File from = new File(path);
//            from.renameTo(to);
//            photos.set(index, attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
//        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        getLocation();
        Log.e("0 :", String.valueOf(locationPoint[0]));
        Log.e("1 :", String.valueOf(locationPoint[1]));
        String imageFileName = "caption_" + timeStamp + "_" + locationPoint[0] + "_" + locationPoint[1] + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("asdf", mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("request code", String.valueOf(requestCode));

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
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
                photos = findPhotos(startTimestamp, endTimestamp, keywords,
                        minLongitude, maxLongitude, minLatitude, maxLatitude);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0, 0, 0, 0);
        }
    }

    private void locationPermissionCheck() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
//        if (ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
//                        longitude.append(String.valueOf(location.getLongitude()));
//                        latitude.append(String.valueOf(location.getLatitude()));
//                        Log.d("location-longitude", String.valueOf(location.getLongitude()));
//                        Log.d("location-latitude", String.valueOf(location.getLatitude()));
                        locationPoint[0] = location.getLongitude();
                        locationPoint[1] = location.getLatitude();
                        Log.d("00", String.valueOf(locationPoint[0]));
                        Log.d("10", String.valueOf(locationPoint[1]));
                    }
                }

            });
        } else {
            longitude.setText("longitude: unknown");
            latitude.setText("latitude: unknown");
            Log.d("location permission", "not granted");
        }
    }
}