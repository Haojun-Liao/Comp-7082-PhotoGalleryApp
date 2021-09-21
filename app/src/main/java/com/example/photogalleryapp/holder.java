//package com.example.photogalleryapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//public class MainActivity extends AppCompatActivity {
//    private ArrayList<String> photos = null;
//    static final int REQUEST_IMAGE_CAPTURE = 1, SEARCH_ACTIVITY_REQUEST_CODE = 2;
//    private int index = 0;
//    String mCurrentPhotoPath;
//    ImageView imageView;
//    Button snapButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        imageView = findViewById(R.id.imageView);
//        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
//        snapButton = findViewById(R.id.snapButton);
//        snapButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    File photoFile = null;
//                    try {
//                        photoFile = createImageFile();
//                    } catch (IOException ex) {
//                        // Error occurred while creating the File
//                    }
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
//                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.photogalleryapp.fileprovider", photoFile);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//                    }
//                }
//            }
//        });
//        if (photos.size() == 0) {
//            displayPhoto(null);
//        } else {
//            displayPhoto(photos.get(index));
//        }
//    }
//
//    //manually put in a new code for search button in activity_main.xml
//    public void openSearch(View v) {
//        startActivity(new Intent(MainActivity.this, SearchActivity.class));
//    }
//
//    public void scrollPhotos(View v) {
//        if (photos.size() == 0) {
//            return;
//        }
//        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
//        switch (v.getId()) {
//            case R.id.btnPrev:
//                if (index > 0) {
//                    index--;
//                }
//                break;
//            case R.id.btnNext:
//                if (index < (photos.size() - 1)) {
//                    index++;
//                }
//                break;
//            default:
//                break;
//        }
//        Log.e("current index:", String.valueOf(index));
//        displayPhoto(photos.get(index));
//    }
//    private void displayPhoto(String path) {
//        ImageView iv = (ImageView) findViewById(R.id.imageView);
//        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
//        EditText et = (EditText) findViewById(R.id.etCaption);
//        if (path == null || path == "") {
//            iv.setImageResource(R.mipmap.ic_launcher);
//            et.setText("");
//            tv.setText("");
//        } else {
//            iv.setImageBitmap(BitmapFactory.decodeFile(path));
//            String[] attr = path.split("_");
//            et.setText(attr[1]);
//            tv.setText(attr[2]);
//        }
//    }
//    private void updatePhoto(String path, String caption) {
//        String[] attr = path.split("_");
//        if (attr.length >= 3) {
//            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
//            File from = new File(path);
//            from.renameTo(to);
//            photos.set(index, attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
//        }
//    }
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "caption_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
//        mCurrentPhotoPath = image.getAbsolutePath();
//        Log.e("asdf", mCurrentPhotoPath);
//
//        return image;
//    }
//    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
//        File file = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures");
//        ArrayList<String> photos = new ArrayList<String>();
//        File[] fList = file.listFiles();
//        if (fList != null) {
//            for (File f : fList) {
//                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
//                        && f.lastModified() <= endTimestamp.getTime())
//                ) && (keywords == "" || f.getPath().contains(keywords)))
//                    photos.add(f.getPath());
//            }
//        }
//        return photos;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
//                Date startTimestamp , endTimestamp;
//                try {
//                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
//                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
//                    startTimestamp = format.parse(from);
//                    endTimestamp = format.parse(to);
//                } catch (Exception ex) {
//                    startTimestamp = null;
//                    endTimestamp = null;
//                }
//                String keywords = (String) data.getStringExtra("KEYWORDS");
//                index = 0;
//                photos = findPhotos(startTimestamp, endTimestamp, keywords);
//                if (photos.size() == 0) {
//                    displayPhoto(null);
//                } else {
//                    displayPhoto(photos.get(index));
//                }
//            }
//        }
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
//        }
//    }
//}