package com.example.cpu10152_local.testrecyclerview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cpu10152_local.testrecyclerview.adapter.MyAdapter;
import com.example.cpu10152_local.testrecyclerview.entity.Movie;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ID = 100;
    private static final String TAG = "TAG";
    RecyclerView rv;
    List<Movie> movies;
    MyAdapter adapter;
    RelativeLayout progressBar;
    private final int RequestPermissionCode = 96;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        adapter = new MyAdapter(this);
//        movies = new ArrayList<>();
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        rv = findViewById(R.id.rv);
//        rv.setLayoutManager(manager);
//        rv.setAdapter(adapter);
//        rv.setHasFixedSize(true);
//        rv.setDrawingCacheEnabled(true);
//        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//
//        initMovies();
//
//        adapter.setMovies(movies);

        progressBar = findViewById(R.id.progress);

        findViewById(R.id.btn_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = DialogPicker.getPickImageIntent(MainActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                final Bitmap bitmap = DialogPicker.getImageFromResult(this, resultCode, data);

                if (bitmap != null) {
                    new AsyncTask<Bitmap, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected String doInBackground(Bitmap... bitmaps) {
                            Log.d(TAG, "Begin encoding!");
                            Bitmap current = bitmaps[0];

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            current.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream .toByteArray();

                            return Base64.encodeToString(byteArray, Base64.DEFAULT);
                        }

                        @Override
                        protected void onPostExecute(String s) {

                            new AsyncTask<String, Void, Bitmap>() {
                                @Override
                                protected Bitmap doInBackground(String... strings) {
                                    String encoded = strings[0];

                                    byte[] decodedBytes = Base64.decode(encoded, 0);
                                    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                }

                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    Glide.with(MainActivity.this)
                                            .load(bitmap)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into((ImageView) findViewById(R.id.background));
                                    Log.d(TAG, "Finish decoding");
                                    progressBar.setVisibility(View.GONE);
                                }
                            }.execute(s);

                        }
                    }.execute(bitmap);
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        ACCESS_NETWORK_STATE,
                        INTERNET
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessNetworkPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && WriteStoragePermission && ReadStoragePermission && AccessNetworkPermission && InternetPermission) {

                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void initMovies() {
        movies.add(new Movie("Big Hero 6", "When a devastating event befalls the city of San Fransokyo and catapults Hiro into the midst of danger, he turns to Baymax and his close friends adrenaline junkie Go Go Tomago, neatnik Wasabi, chemistry whiz Honey Lemon and fanboy Fred. Determined to uncover the mystery, Hiro transforms his friends into a band of high-tech heroes called \"Big Hero 6.\"", "http://static.tvtropes.org/pmwiki/pub/images/bighero6_team.jpg"));
        movies.add(new Movie("Batman: Gotham by Gaslight",
                "In an alternative Victorian Age Gotham City, Batman begins his war on crime while he investigates a new series of murders by Jack the Ripper. ",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTLrbQNiWA2Z-giFSF6YxFJCuzNs-Q46Ni5Myls1gKqlLhP6qd6"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
        movies.add(new Movie("How to train your dragon",
                "A hapless young Viking who aspires to hunt dragons becomes the unlikely friend of a young dragon himself, and learns there may be more to the creatures than he assumed. ",
                "http://cdn.shopify.com/s/files/1/0799/0083/products/Various_HowToTr_CoverAr_3000DPI300RGB1000144210_grande.jpg?v=1460676017"));
    }
}
