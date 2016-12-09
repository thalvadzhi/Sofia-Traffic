package com.bearenterprises.sofiatraffic;

import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.fragments.FavouritesFragment;
import com.bearenterprises.sofiatraffic.fragments.LoadingFragment;
import com.bearenterprises.sofiatraffic.fragments.LocationFragment;
import com.bearenterprises.sofiatraffic.fragments.LocationResultsFragment;
import com.bearenterprises.sofiatraffic.fragments.MapFragment;
import com.bearenterprises.sofiatraffic.fragments.ResultsFragment;
import com.bearenterprises.sofiatraffic.fragments.SearchFragment;
import com.bearenterprises.sofiatraffic.fragments.communication.StationTimeShow;
import com.bearenterprises.sofiatraffic.location.GPSTracker;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.stations.VehicleTimes;
import com.bearenterprises.sofiatraffic.updater.DbUpdater;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements StationTimeShow, ActivityCompat.OnRequestPermissionsResultCallback, OnDismissCallback {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private GoogleApiClient client;
    private GoogleApiClient mGoogleApiClient;
    public GPSTracker tracker;
    private LocationRequest mLocationRequest;
    private BackupManager backupManager;
    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500;
    private FavouritesFragment favouritesFragment;
    private SearchFragment searchFragment;
    private LocationFragment locationFragment;
    private ProgressDialog dialog;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            favouritesFragment = FavouritesFragment.newInstance();
            searchFragment = SearchFragment.newInstance();
            locationFragment = LocationFragment.newInstance();
        }
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        dialog = new ProgressDialog(this);
        backupManager = new BackupManager(this);
        //Check if station info needs updating
        DbUpdater updater = new DbUpdater(this);
        updater.execute();

        tracker = new GPSTracker(this);
        createLocationRequest();
        buildGoogleApiClient(tracker);
        tracker.setClient(mGoogleApiClient);
//        tracker.startUpdatesButtonHandler();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        makeSnackbar("Няма сиренце :(");
                        return;
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, tracker);
                }else{
                    Utility.toastOnUiThread("Ни моа та намеря без GPS, бе!", this);
                }
                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected synchronized void buildGoogleApiClient(GPSTracker tracker) {
        Log.i("BALI", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(tracker)
                .addOnConnectionFailedListener(tracker)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void makeSnackbar(String message){
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .show();
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

    public CoordinatorLayout getCoordinatorLayout(){
        return coordinatorLayout;
    }

    public BackupManager getBackupManager(){
        return backupManager;
    }
    public void setPage (int index){
        mViewPager.setCurrentItem(index, true);
    }

    public void notifyDatasetChanged(){
        mSectionsPagerAdapter.notifyDataSetChanged();
    }



    public void addFavourite(Station st){
        favouritesFragment.addFavourite(st);
    }

    public void removeFavourite(String code){
        favouritesFragment.removeFavourite(code);
    }


    public void changeFragment(int id, Fragment fragment){
        getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).

                replace(id, fragment).
                commit();
    }

    public void changeFragmentNotSupport(int id, android.app.Fragment fragment){
        getFragmentManager().
                beginTransaction().

                replace(id, fragment).
                commit();
    }

    public void changeFragmentAddBackStack(int id, Fragment fragment){
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(String.valueOf(id)).
                setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,R.anim.slide_in_right, R.anim.slide_out_left ).
                replace(id, fragment).
                commit();
    }

    public void detachFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
        tracker.stopUpdatesButtonHandler();
        mGoogleApiClient.disconnect();
    }

    public ArrayList<Station> getStationByCode(String code) throws SQLiteDatabaseLockedException{
        String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + " =?";
        String[] args = new String[]{code};
        return getStationsFromDatabase(query, args);

    }

    public ArrayList<Station> getEveryStation()throws SQLiteDatabaseLockedException{
        String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME;
        return getStationsFromDatabase(query, new String[]{});
    }

    public ArrayList<Station> getStationsFromDatabase(String query, String[] codes) throws SQLiteDatabaseLockedException{
        DbManipulator manipulator=null;
        try {
            manipulator = new DbManipulator(this);
        }catch (SQLiteDatabaseLockedException e){
            if(manipulator != null){
                manipulator.closeDb();
            }
            throw e;

        }

        ArrayList<Station> stations = new ArrayList<>();
        try(Cursor c = manipulator.readRawQuery(query, codes)){
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
            } else {
                Utility.makeSnackbar("Няма такава спирка", coordinatorLayout);
                return null;
            }
            String stationName = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME));
            String stationCode = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_CODE));
            String lat = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LAT));
            String lon = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_LON));
            String description = c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION));

            stations.add(new Station(stationName, stationCode, lat, lon, description));
        }finally {
            if(manipulator != null){
                manipulator.closeDb();

            }
        }
        return stations;
    }

    public void showOnMap(ArrayList<Station> stations){
        setPage(Constants.SECTION_MAP_IDX);
        MapFragment f = MapFragment.newInstance(stations, null);
        changeFragmentAddBackStack(R.id.location_container, f);
    }

    public void showOnMap(Station st){
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(st);
        showOnMap(stations);
    }


    @Override
    public void onDismiss(ViewGroup listView, int[] reverseSortedPositions) {

    }

    @Override
    public void showTimes(String code) {
        setPage(Constants.SECTION_SEARCH_IDX);
        searchFragment.showStationTimes(code);
    }

    public void addTimes(ResultsFragment fragment, Line line, List<Time> times){
        fragment.addTimeSchedule(line, (ArrayList<Time>) times);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == Constants.SECTION_SEARCH_IDX){
                return searchFragment;
            }else if(position == Constants.SECTION_FAVOURITES_IDX){
                return favouritesFragment;
            }else if(position == Constants.SECTION_MAP_IDX){
                return locationFragment;
            }
            return searchFragment;
        }
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return Constants.SECTION_SEARCH;
                case 1:
                    return Constants.SECTION_FAVOURITES;
                case 2:
                    return Constants.SECTION_MAP;
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
                searchFragment = (SearchFragment) super.instantiateItem(container, position);
                return searchFragment;
            }else if(position == Constants.SECTION_FAVOURITES_IDX){
                favouritesFragment = (FavouritesFragment) super.instantiateItem(container, position);
                return favouritesFragment;
            }else {
                locationFragment = (LocationFragment) super.instantiateItem(container, position);
                return locationFragment;
            }
        }
    }
}
