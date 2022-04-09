package com.cipherapps.breathingmeditation.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.fragments.BreathingTime_Fragment;
import com.cipherapps.breathingmeditation.fragments.ControlsFragment;
import com.cipherapps.breathingmeditation.savedata.SharePrefDataSave;
import com.cipherapps.breathingmeditation.viewmodels.BreathingTimeViewModel;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private ControlsFragment controlsFragment;
    private BreathingTime_Fragment breathingTime_fragment;
    private BreathingTimeViewModel viewModel;
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();
    private boolean flag = false;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.rateUs_mainMenu:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        getString(R.string.appplayStoreLink)));
                startActivity(intent);
                return true;
            case R.id.newSession_mainMenu:
                breathingTime_fragment.resetAll();
                return true;
            case R.id.journals_mainMenu:
                startActivity(new Intent(MainActivity.this, ShowAllSaveSesionActivity.class));
                return true;
            case R.id.settings_mainMenu:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                flag = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void initializeView() {

        bottomNavigationView = findViewById(R.id.bottom_navigationMain);
        breathingTime_fragment = new BreathingTime_Fragment();
        controlsFragment = new ControlsFragment();

        viewModel = new ViewModelProvider(this).get(BreathingTimeViewModel.class);
        viewModel.setContext(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.breathingTimeRule_fragmentContainer, breathingTime_fragment);
        ft.replace(R.id.control_fragmentContainer, controlsFragment);
        ft.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);
        bottomNavigationView.setSelectedItemId(R.id.home_tab);

        MobileAds.initialize(this);
        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-6834680172649663/4465181395")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);

                        template.setNativeAd(nativeAd);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
        loadAd();
        observersViewModels();

    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home_tab);
        if (flag) {
            breathingTime_fragment.resetAll();
            flag = false;
        }
    }

    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
        switch (item.getItemId()) {
            case R.id.home_tab:
                return true;

            case R.id.setting_tab:
//                setAlarm();
                flag = true;
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return false;
    }

    private void observersViewModels(){
        viewModel.showAds.observe(this, value ->{
            if(value){
               showInterstitial();
            }
        });
    }


    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.interstitialAd = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                    }
                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        interstitialAd = null;
                    }
                });
    }
    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
            loadAd();
        } else {
            loadAd();
        }
    }
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6834680172649663/7474488116";
}