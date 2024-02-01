package com.kemoi.kvwallpaper;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class WelcomeActivity extends AppCompatActivity  implements View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    private final int[] layouts = new int[]{R.layout.slide1, R.layout.slide2, R.layout.slide3, R.layout.slide4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.init(this);
        // Checking for first time launch - before calling setContentView()
        if (!Constants.shouldShowSlider()) {
            launchHomeScreen();
            finish();
        }


        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (window != null) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);

        setBtnClickListener(R.id.btn_got_it);
        setBtnClickListener(R.id.btn_next);
        setBtnClickListener(R.id.btn_skip);
        setBtnClickListener(R.id.email);
        setBtnClickListener(R.id.install);
        setBtnClickListener(R.id.installs);

        // adding bottom dots
        addBottomDots();
        // By default, select dot in the first position
        updateBottomDots(0, 0);

        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.addOnPageChangeListener(pageChangeListener);
    }

    private void setBtnClickListener(int id) {
        Button button = findViewById(id);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    private void showHideView(int id, int visibility) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void launchHomeScreen() {
        //TODO replace true with false
        Constants.saveFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    private void addBottomDots() {
        if ((dotsLayout == null) || (layouts == null))
            return;

        int dotSize = layouts.length;
        dotsLayout.removeAllViews();

        dots = new TextView[dotSize];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dotsLayout.addView(dots[i]);
        }
    }

    private void updateBottomDots(int prevPosition, int curPosition) {
        if (dots == null)
            return;

        int dotLength = dots.length;
        if ((dotLength > prevPosition) && (dotLength > curPosition)) {
            dots[prevPosition].setTextColor(getResources().getColor(R.color.dot_inactive));
            dots[curPosition].setTextColor(getResources().getColor(R.color.dot_active));
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        int prevPos = 0;

        @Override
        public void onPageSelected(int position) {

            updateBottomDots(prevPos, position);

            boolean isLastPage = (position == (layouts.length - 1));
            showHideView(R.id.btn_next, isLastPage ? View.GONE : View.VISIBLE);
            showHideView(R.id.btn_skip, isLastPage ? View.GONE : View.VISIBLE);
            showHideView(R.id.btn_got_it, isLastPage ? View.VISIBLE : View.GONE);

            prevPos = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip:
            case R.id.btn_got_it:
                launchHomeScreen();
                break;
            case R.id.btn_next:
                showNextSlide();
                break;
        }
    }

    private void showNextSlide() {
        // Checking for last page
        // If last page home screen will be launched
        int nextIndex = viewPager.getCurrentItem() + 1;
        if ((viewPager != null) && (nextIndex < layouts.length)) {
            viewPager.setCurrentItem(nextIndex);
        }
    }
    private void status(){
        Uri uri = Uri.parse("market://details?id=com.evanaron.statussaverwhatsappfacebook");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (
                ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.evanaron.statussaverwhatsappfacebook")));
        }
    }
    @SuppressLint("IntentReset")
    private void email(){
        try {
            //Intent intent = new Intent(Intent.ACTION_SENDTO);

            final Intent intent = new Intent(Intent.ACTION_SEND);
              //intent.setData(Uri.parse("mailto:kayvanscodes@gmail.com"));
              intent.setType("message/rfc822");
              intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kayvanscodes@gmail.com"});
              intent.putExtra(Intent.EXTRA_SUBJECT, "Help on: ");
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final PackageManager pm = getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") ||
                        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
            if (best != null)
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(WelcomeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    private void music(){
        Uri uri = Uri.parse("market://details?id=com.kyavanscodessongs.music");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (
                ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.kyavanscodessongs.music")));
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private final LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = layoutInflater.inflate(layouts[position], container, false);
            if (position == 1){
                Button button = view.findViewById(R.id.install);
                button.setOnClickListener(v -> status());
            }
            else if(position == 2){
                Button button = view.findViewById(R.id.installs);
                button.setOnClickListener(v -> music());
            }
            else if (position == 3){
                Button button = view.findViewById(R.id.email);
                button.setOnClickListener(v -> email());
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return (layouts != null) ? layouts.length : 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, Object obj) {
            return (view == obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}