package com.kemoi.kvwallpaper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kemoi.kvwallpaper.Adapters.CuratedAdapter;
import com.kemoi.kvwallpaper.Listeners.CuratedResponseListener;
import com.kemoi.kvwallpaper.Listeners.OnRecyclerClickListener;
import com.kemoi.kvwallpaper.Models.CuratedApiResponse;
import com.kemoi.kvwallpaper.Models.Photo;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnRecyclerClickListener {

    public static RecyclerView recyclerView_home;

    public static CuratedAdapter adapter;
    public static ProgressDialog dialog;
    public static RequestManager manager;
    FloatingActionButton fab_next,fab_prev;

    private AdView mAdView;
    int page;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home2, container, false);
        fab_next = view.findViewById(R.id.fab_next);
        fab_prev = view.findViewById(R.id.fab_prev);
        recyclerView_home = view.findViewById(R.id.recycler_home);

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading...");



        final CuratedResponseListener listener = new CuratedResponseListener() {
            @Override
            public void onFetch(CuratedApiResponse response, String message) {
                dialog.dismiss();
                if (response.getPhotos().isEmpty()){
                    Toast.makeText(getActivity(), "No Image Found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                page = response.getPage();
                showData(response.getPhotos());
            }

            @Override
            public void onError(String message) {
                dialog.dismiss();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            }
        };

        manager = new RequestManager(getContext());
        manager.getCuratedWallpapers(listener,"1");

        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String next_page = String.valueOf(page+1);
                manager.getCuratedWallpapers(listener,next_page);
                dialog.show();
            }
        });

        fab_prev.setOnClickListener(v -> {
            if (page>1){
                String prev_page = String.valueOf(page-1);
                manager.getCuratedWallpapers(listener,prev_page);
                dialog.show();
            }
        });
        return view;

    }

    public void showData(ArrayList<Photo> photos) {
        recyclerView_home.setHasFixedSize(true);
        recyclerView_home.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new CuratedAdapter(getActivity(), photos, this );
        recyclerView_home.setAdapter(adapter);
    }

    //onClick photo starts wallpaper activity
    @Override
    public void onClick(Photo photo) {
        try{
            startActivity(new Intent(getActivity(),WallpaperActivity.class)
                    .putExtra("photo",photo));
        }
        catch (Exception e){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }

    }
}