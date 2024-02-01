package com.kemoi.kvwallpaper;

import android.Manifest;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kemoi.kvwallpaper.Models.Photo;
import com.squareup.picasso.Picasso;

public class WallpaperActivity extends AppCompatActivity  implements View.OnClickListener {

    ImageView imageview_wallpaper;
    FloatingActionButton fab_download,fab_wallpaper;
    Photo photo;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wallpaper);
        //initialization for ads
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //intestitial ads

        //interstitial ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        //AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-6085853804536174/7681273180", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                              }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });


        imageview_wallpaper = findViewById(R.id.imageview_wallpaper);
        fab_download = findViewById(R.id.fab_download);
        fab_wallpaper = findViewById(R.id.fab_wallpaper);


        checkPermission();
        try{
            photo = (Photo) getIntent().getSerializableExtra("photo");
            Picasso.get().load(photo.getSrc().getOriginal()).placeholder(R.drawable.placeholder).into(imageview_wallpaper);
        }
        catch (Exception e){
            Toast.makeText(WallpaperActivity.this, "Could not load this image. Try another image.", Toast.LENGTH_SHORT).show();
        }


        fab_download.setOnClickListener(v -> {
            DownloadManager downloadManager;
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            Uri uri = Uri.parse(photo.getSrc().getLarge());

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("cool_wallpaper_"+photo.getPhotographer())
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"CoolWallpaper_"+photo.getPhotographer()+".jpg");
            downloadManager.enqueue(request);

            Toast.makeText(WallpaperActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
        });


        fab_wallpaper.setOnClickListener(v -> {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperActivity.this);
            Bitmap bitmap = ((BitmapDrawable) imageview_wallpaper.getDrawable()).getBitmap();

            try {
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(WallpaperActivity.this, "Wallpaper Applied!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(WallpaperActivity.this, "An error occurred. Try again later!", Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void onClick(View view) {
        download();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mInterstitialAd != null){
            mInterstitialAd.show(WallpaperActivity.this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    finish();
                }
            });
        }else {
            finish();
        }
    }

    private void download() {
    }
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(WallpaperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(WallpaperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(WallpaperActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Storage permission is required to save images");
//
                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(WallpaperActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });

                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(WallpaperActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}