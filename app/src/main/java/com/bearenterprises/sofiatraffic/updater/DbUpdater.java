package com.bearenterprises.sofiatraffic.updater;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.bearenterprises.sofiatraffic.utilities.db.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.db.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.parsing.Description;
import com.bearenterprises.sofiatraffic.utilities.parsing.DescriptionsParser;
import com.bearenterprises.sofiatraffic.utilities.network.FileDownloader;
import com.bearenterprises.sofiatraffic.utilities.parsing.JSONParser;
import com.bearenterprises.sofiatraffic.utilities.Utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bearenterprises.sofiatraffic.R.id.coordinates;

/**
 * Created by thalv on 08-Jul-16.
 */
public class DbUpdater extends AsyncTask<Void, String, Void>{
    private Context context;
    private boolean fileDownloaderExceptionHappened;
    private CoordinatorLayout coordinatorLayout;

    public DbUpdater(Context context) {
        this.context = context;
        this.fileDownloaderExceptionHappened = false;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences preferences = this.context.getSharedPreferences(Constants.SHARED_PREFERENCES_LAST_UPDATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        long lastUpdate = preferences.getLong(Constants.KEY_LAST_UPDATE, Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);


        if (shouldUpdate(lastUpdate)){
            //means it's time to update

            try {
                boolean wasUpdated = update();
                if(wasUpdated) {
                    editor.putLong(Constants.KEY_LAST_UPDATE, System.currentTimeMillis());
                    editor.commit();
                    Utility.makeSnackbar("Информацията за спирките беше обновена!", (MainActivity)context);
                }
            } catch (Exception e) {
                Utility.makeSnackbar("Информацията за спирките НЕ беше обновена :(", (MainActivity)context);
                publishProgress(Constants.DISMISS_DIALOG);
            }
            publishProgress(Constants.DISMISS_DIALOG);



        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... strings){
        String whatToDo = strings[0];
        if(whatToDo.equals(Constants.SHOW_DIALOG)){
            ((MainActivity)context).showLoadingStopsInfoDialog();
        }else if(whatToDo.equals(Constants.DISMISS_DIALOG)){
            ((MainActivity)context).dismissLoadingStopsInfoDialog();
        }
    }




    /*
        Downloads the new file and checks if it has changed
        returns true if file has changed or it is the first time that the file is created
        returns false otherwise
     */
    private boolean handleFileDownloading(String fileName) throws IOException {
        String newFile = null, downloadUrl = null;
        switch (fileName){
            case Constants.JSON_COORDINATE_FILE:
                newFile = Constants.JSON_COORDINATE_FILE_NEW;
                downloadUrl = Constants.COORDINATES_DOWNLOAD_URL_JSON;
                break;
            case Constants.DESCRIPTIONS_FILE_NAME:
                newFile = Constants.DESCRIPTIONS_FILE_NAME_NEW;
                downloadUrl = Constants.DESCRIPTIONS_DOWNLOAD_URL;
        }
        File f = new File(context.getFilesDir() + File.separator + fileName);
        if(f.exists()){
            File new_f = new File(context.getFilesDir() + File.separator + newFile);
            FileDownloader downloader = new FileDownloader(this.context, downloadUrl, new_f);
            downloader.download();
            if(FileUtils.contentEquals(f, new_f)){
                new_f.delete();
                return false;
            }else{
                f.delete();
                new_f.renameTo(new File(context.getFilesDir() + File.separator + fileName));
            }
        }else{
            FileDownloader downloader = new FileDownloader(this.context, downloadUrl, f);
            downloader.download();
        }
        return true;

    }

    public boolean update() throws Exception{
        boolean updatedCoordinates, updatedDescriptions ;
        try{
            updatedCoordinates = handleFileDownloading(Constants.JSON_COORDINATE_FILE);
            updatedDescriptions = handleFileDownloading(Constants.DESCRIPTIONS_FILE_NAME);
        }catch (IOException e){
            Utility.makeSnackbar("Настъпи грешка при изтеглянето", (MainActivity) context);
            return false;
        }

        if (!updatedCoordinates && !updatedDescriptions){
            return false;
        }

        publishProgress(Constants.SHOW_DIALOG);



        List<Description> descriptions = DescriptionsParser.parseDescriptions(this.context, Constants.DESCRIPTIONS_FILE_NAME);

        ArrayList<Stop> stations = null;
        stations = JSONParser.getStationsFromFile(Constants.JSON_COORDINATE_FILE, this.context);
        ArrayList<ContentValues> stationInformation = new ArrayList<>();
        if(stations == null){
            throw new Exception("stations array is null");
        }
        for (Stop station : stations) {
            ContentValues v = new ContentValues();
            v.put(DbHelper.FeedEntry.COLUMN_NAME_CODE, station.getCode());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME, station.getName());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LAT, station.getLatitude());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LON, station.getLongtitude());

            stationInformation.add(v);
        }

        ArrayList<ContentValues> descriptionContentValues = new ArrayList<>();
        for (Description desc : descriptions){
            ContentValues v = new ContentValues();
            v.put(DbHelper.FeedEntry.COLUMN_NAME_TR_TYPE, desc.getTransportationType());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LINE_NAME, desc.getLineName());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_STOP_CODE, desc.getStopCode());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_DIRECTION, desc.getDirection());
            descriptionContentValues.add(v);
        }

        DbManipulator manipulator = new DbManipulator(this.context);
        manipulator.deleteAll();
        manipulator.insert(stationInformation, DbHelper.FeedEntry.TABLE_NAME_STATIONS);
        manipulator.insert(descriptionContentValues, DbHelper.FeedEntry.TABLE_NAME_DESCRIPTIONS);
        manipulator.closeDb();

        return true;

    }

    private boolean shouldUpdate(long lastUpdate){
//        return true;
        long delta = System.currentTimeMillis() - lastUpdate;
        return (delta > Constants.DAY_IN_MILLISECONDS || lastUpdate == Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);
    }

}
