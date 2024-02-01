package com.kemoi.kvwallpaper;


import static com.kemoi.kvwallpaper.HomeFragment.recyclerView_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.kemoi.kvwallpaper.Adapters.CuratedAdapter;
import com.kemoi.kvwallpaper.Listeners.CuratedResponseListener;
import com.kemoi.kvwallpaper.Listeners.OnRecyclerClickListener;
import com.kemoi.kvwallpaper.Listeners.SearchResponseListener;
import com.kemoi.kvwallpaper.Models.CuratedApiResponse;
import com.kemoi.kvwallpaper.Models.Photo;
import com.kemoi.kvwallpaper.Models.SearchApiResponse;
import com.kemoi.kvwallpaper.databinding.ActivityMainPageBinding;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements OnRecyclerClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainPageBinding binding;

    ProgressDialog dialog;

    RequestManager manager;
    private InterstitialAd mInterstitialAd;


    int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-6085853804536174/7681273180", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        //Toast.makeText(MainPage.this, "These and many more!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        final CuratedResponseListener listener = new CuratedResponseListener() {
            @Override
            public void onFetch(CuratedApiResponse response, String message) {
                dialog.dismiss();
                if (response.getPhotos().isEmpty()){
                    Toast.makeText(MainPage.this, "No Image Found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                page = response.getPage();
                showData(response.getPhotos());
            }

            @Override
            public void onError(String message) {
                dialog.dismiss();
                Toast.makeText(MainPage.this, message, Toast.LENGTH_SHORT).show();

            }
        };


        manager = new RequestManager(this);
        manager.getCuratedWallpapers(listener, "1");
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainPage.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }



    private void showData(ArrayList<Photo> photos) {
        recyclerView_home.setHasFixedSize(true);
        recyclerView_home.setLayoutManager(new GridLayoutManager(this,2));
        HomeFragment.adapter = new CuratedAdapter(this, photos, this);
        recyclerView_home.setAdapter(HomeFragment.adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    //Search bar activities
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Wallpaper");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                manager.searchCuratedWallpapers(searchResponseListener,"1",query);
                dialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mInterstitialAd != null){
            mInterstitialAd.show(MainPage.this);
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


    private final SearchResponseListener searchResponseListener = new SearchResponseListener() {
        @Override
        public void onFetch(SearchApiResponse response, String message) {

            //after fetch remove dialog and load image
            //no image is picked

            dialog.dismiss();
            if (response.getPhotos().isEmpty()) {
                Toast.makeText(MainPage.this, "No Image Found!", Toast.LENGTH_SHORT).show();
                return;
            }
            showData(response.getPhotos());
        }

        //
        @Override
        public void onError(String message) {
            dialog.dismiss();
            Toast.makeText(MainPage.this, message, Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onClick(Photo photo) {
        startActivity(new Intent(this,WallpaperActivity.class)
                .putExtra("photo",photo));
    }
}