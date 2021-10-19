package com.example.photogalleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainView extends AppCompatActivity implements MVPContract.View {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SEARCH_RESULT = 2;
    private MVPContract.Presenter presenter;

    ImageView imageView;
    TextView tv, tvLongitude, tvLatitude;
    EditText et;
    Button snapButton, shareBtn;

    /** Proxy - Client FileManager */
    IFileManager proxyFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.tvTimestamp);
        et = (EditText) findViewById(R.id.etCaption);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        snapButton = findViewById(R.id.snapButton);
        shareBtn = findViewById(R.id.share);
        presenter = Presenter.getInstance(this, new Model());

        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationPermissionCheck();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    presenter.capture(MainView.this);
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                presenter.shareImage(MainView.this, bitmap);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            presenter.onSuccessfulCapture();
        } else if (requestCode == REQUEST_SEARCH_RESULT && resultCode == RESULT_OK) {
            presenter.onSearchResult(data);
        }
    }

    public void displayPhoto(String path) {
        if (path == null || path == "") {
            imageView.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
            tvLongitude.setText("");
            tvLatitude.setText("");
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
            tvLongitude.setText(attr[3]);
            tvLatitude.setText(attr[4]);
        }

    }

    @Override
    /** Proxy - Service FileManager */
    public void launchCamera(Uri photoUri) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//           intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//           startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//        }
        proxyFileManager = new ProxyFileManager();
        proxyFileManager.saveFile(photoUri, this);
    }

    private void locationPermissionCheck() {
        if (ActivityCompat.checkSelfPermission(MainView.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainView.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    public void scrollPhotos(View v) {
        presenter.updatePhoto(((EditText) findViewById(R.id.etCaption)).getText().toString());
        presenter.scrollPhotos(v);
    }
    public void search(View v) {
        Intent intent = presenter.search(this);
        startActivityForResult(intent, REQUEST_SEARCH_RESULT);
    }
}
