package com.bearenterprises.sofiatraffic.activities;

import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.fragments.FavouritesFragment;
import com.bearenterprises.sofiatraffic.fragments.LinesFragment;
import com.bearenterprises.sofiatraffic.fragments.MapSearchFragment;
import com.bearenterprises.sofiatraffic.fragments.TimesSearchFragment;
import com.bearenterprises.sofiatraffic.restClient.Registration;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.updater.DbUpdater;
import com.bearenterprises.sofiatraffic.utilities.network.GenerateClient;
import com.bearenterprises.sofiatraffic.utilities.registration.RegistrationUtility;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Stack;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private GoogleApiClient client;
    private BackupManager backupManager;
    private FavouritesFragment favouritesFragment;
    private MapSearchFragment mapSearchFragment;
    private TimesSearchFragment timesSearchFragment;
    private LinesFragment linesFragment;
    private ProgressDialog dialog;
    private Registration registration;
    private Stack<Integer> pageHistory;
    private boolean saveToHistory;
    private CoordinatorLayout coordinatorLayout;
    private int currentPage;
    public static Retrofit retrofit;
    private static String queryMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            //this is so as to resume app when clicking on app icon instead of starting it again
            finish();
            return;
        }

        if(savedInstanceState == null){
            favouritesFragment = FavouritesFragment.newInstance();
            timesSearchFragment = TimesSearchFragment.newInstance();
            linesFragment = LinesFragment.newInstance();
            mapSearchFragment = new MapSearchFragment();
        }

        Utility.setTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        initializeRetrofitInstance();
        RegistrationUtility.handleRegistration(this);
        GenerateClient.setRegistration(registration);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        dialog = new ProgressDialog(this);
        backupManager = new BackupManager(this);
        //Check if station info needs updating
        DbUpdater updater = new DbUpdater(this);
        updater.execute();

        currentPage = 0;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position != Constants.SECTION_SEARCH_IDX) {
                    hideSoftKeyboad();
                }

                if(saveToHistory){
                    pageHistory.push(currentPage);
                    currentPage = position;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pageHistory = new Stack<>();
        saveToHistory = true;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setFavouritePage();
        setQueryMethod();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public static void setQueryMethod(String method){
        queryMethod = method;
    }

    public void hideSoftKeyboad(){
        final InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void initializeRetrofitInstance(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.IVKOS_API_BASE_URL)
                .client(GenerateClient.getClient(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    private void setFavouritePage(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String startupScreen = sharedPref.getString(getResources().getString(R.string.key_choose_startup_screen), getResources().getString(R.string.default_startup_screen));
        String search = getResources().getString(R.string.settings_search_value);
        String favorites = getResources().getString(R.string.settings_favourites_value);
        String lines = getResources().getString(R.string.settings_lines_value);
        String map = getResources().getString(R.string.settings_map_value);
        if(startupScreen.equals(search)){
            setPage(Constants.SECTION_SEARCH_IDX);
        }else if(startupScreen.equals(favorites)){
            setPage(Constants.SECTION_FAVOURITES_IDX);
        }else if(startupScreen.equals(lines)){
            setPage(Constants.SECTION_LINES_IDX);
        }else if(startupScreen.equals(map)){
            setPage(Constants.SECTION_MAP_SEARCH_IDX);
        }
    }

    private void setQueryMethod(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        queryMethod = sharedPreferences.getString(getResources().getString(R.string.key_choose_query_method), getResources().getString(R.string.query_method_default));
    }

    public String getQueryMethod(){
        return queryMethod;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Utility.makeSnackbar("Няма сиренце :(", this);
                        return;
                    }
                    mapSearchFragment.getMapFragment().getMap();
                }else{
                    Utility.toastOnUiThread("Без достъп до местоположението някои от функциите няма да работят правилно", this);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    public void showLoadingStopsInfoDialog(){
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Информацията за спирките се обновява. Моля изчакайте.");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismissLoadingStopsInfoDialog(){
        dialog.dismiss();
    }

    public void setPage (int index){
        mViewPager.setCurrentItem(index, true);
    }

    public void showSlideUpPanelWithInfo(Stop stop){
        mapSearchFragment.showMoreInfoInSlideUp(stop);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bearenterprises.sofiatraffic/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bearenterprises.sofiatraffic/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if(pageHistory.empty()){
            super.onBackPressed();
        }else{
            saveToHistory = false;
            mViewPager.setCurrentItem(pageHistory.pop());
            saveToHistory = true;
        }
    }

    public FavouritesFragment getFavouritesFragment() {
        return favouritesFragment;
    }

    public MapSearchFragment getMapSearchFragment() {
        return mapSearchFragment;
    }

    public TimesSearchFragment getTimesSearchFragment() {
        return timesSearchFragment;
    }

    public LinesFragment getLinesFragment() {
        return linesFragment;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public CoordinatorLayout getCoordinatorLayout(){
        return coordinatorLayout;
    }

    public BackupManager getBackupManager(){
        return backupManager;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == Constants.SECTION_SEARCH_IDX){
                return timesSearchFragment;
            }else if(position == Constants.SECTION_FAVOURITES_IDX){
                return favouritesFragment;
            }else if(position == Constants.SECTION_LINES_IDX){
                return linesFragment;
            }else if(position == Constants.SECTION_MAP_SEARCH_IDX){
                return mapSearchFragment;
            }
            return timesSearchFragment;
        }
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return Constants.SECTION_SEARCH;
                case 1:
                    return Constants.SECTION_FAVOURITES;
                case 2:
                    return Constants.SECTION_LINES;
                case 3:
                    return Constants.SECTION_MAP_SEARCH;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(position == Constants.SECTION_SEARCH_IDX){
                timesSearchFragment = (TimesSearchFragment) super.instantiateItem(container, position);
                return timesSearchFragment;
            }else if(position == Constants.SECTION_FAVOURITES_IDX){
                favouritesFragment = (FavouritesFragment) super.instantiateItem(container, position);
                return favouritesFragment;
            }else if (position == Constants.SECTION_LINES_IDX) {
                linesFragment = (LinesFragment) super.instantiateItem(container, position);
                return linesFragment;
            }else {
                mapSearchFragment = (MapSearchFragment) super.instantiateItem(container, position);
                return mapSearchFragment;
            }
        }
    }

    public void onResume(){
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
