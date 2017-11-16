package com.bearenterprises.sofiatraffic.updater;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.Stop;
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

/**
 * Created by thalv on 08-Jul-16.
 */
public class DbUpdater extends AsyncTask<Void, String, Void>{
    private Context context;

    public DbUpdater(Context context) {
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences preferences = this.context.getSharedPreferences(Constants.SHARED_PREFERENCES_LAST_UPDATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        long lastUpdate = preferences.getLong(Constants.KEY_LAST_UPDATE, Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);

//if(true){
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

    private boolean handleFileDownloadingWithHash(String fileName) throws IOException {
        String downloadUrl = null, hashUrl = null, hashFileName =null, hashNewFileName=null;
        switch (fileName){
            case Constants.STOPS_COORDINATE_FILE:
                downloadUrl = Constants.COORDINATES_DOWNLOAD_URL_JSON;
                hashUrl = Constants.COORDINATES_HASH_URL;
                hashFileName = Constants.HASH_COORDS;
                hashNewFileName = Constants.HASH_COORDS_NEW;
                break;
            case Constants.DESCRIPTIONS_FILE_NAME:
                downloadUrl = Constants.DESCRIPTIONS_DOWNLOAD_URL;
                hashUrl = Constants.DESCRIPTIONS_HASH_URL;
                hashFileName = Constants.HASH_DESCS;
                hashNewFileName = Constants.HASH_DESCS_NEW;
                break;
            case Constants.SUBWAY_STOPS_FILE:
                downloadUrl = Constants.SUBWAY_STOPS_URL;
                hashUrl = Constants.SUBWAY_HASH_URL;
                hashFileName = Constants.SUBWAY_HASH;
                hashNewFileName = Constants.SUBWAY_HASH_NEW;
                break;
        }
        File hashFile = new File(context.getFilesDir() + File.separator + hashFileName);
        File targetFile = new File(context.getFilesDir() + File.separator + fileName);
        if(hashFile.exists()){
            File newHashFile = new File(context.getFilesDir() + File.separator + hashNewFileName);
            FileDownloader downloader = new FileDownloader(this.context, hashUrl, newHashFile);
            downloader.download();
            if(FileUtils.contentEquals(hashFile, newHashFile)){
                newHashFile.delete();
                return false;
            }else{
                targetFile.delete();
                targetFile = new File(context.getFilesDir() + File.separator + fileName);
                FileDownloader downloaderTarget = new FileDownloader(this.context, downloadUrl, targetFile);
                downloaderTarget.download();
                hashFile.delete();
                newHashFile.renameTo(new File(context.getFilesDir() + File.separator + hashFileName));
            }
        }else{
            FileDownloader downloader = new FileDownloader(this.context, hashUrl, hashFile);
            downloader.download();

            FileDownloader downloaderTargetFile = new FileDownloader(this.context, downloadUrl, targetFile);
            downloaderTargetFile.download();
        }
        return true;

    }
    public boolean update() throws Exception{
        boolean updatedCoordinates, updatedDescriptions, updatedSubway ;
        try{
            updatedCoordinates = handleFileDownloadingWithHash(Constants.STOPS_COORDINATE_FILE);
            updatedDescriptions = handleFileDownloadingWithHash(Constants.DESCRIPTIONS_FILE_NAME);
            updatedSubway = handleFileDownloadingWithHash(Constants.SUBWAY_STOPS_FILE);
        }catch (IOException e){
            Utility.makeSnackbar("Настъпи грешка при изтеглянето", (MainActivity) context);
            return false;
        }

        if (!updatedCoordinates && !updatedDescriptions && !updatedSubway){
            return false;
        }

        publishProgress(Constants.SHOW_DIALOG);



        List<Description> descriptions = DescriptionsParser.parseDescriptions(this.context, Constants.DESCRIPTIONS_FILE_NAME);

        ArrayList<Stop> stations = null;
        stations = JSONParser.getStationsFromFile(Constants.STOPS_COORDINATE_FILE, this.context);
        ArrayList<ContentValues> stationInformation = new ArrayList<>();
        if(stations == null){
            throw new Exception("stations array is null");
        }
        for (Stop station : stations) {
            ContentValues v = new ContentValues();
            v.put(DbHelper.FeedEntry.COLUMN_NAME_CODE, station.getCode());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME, station.getName());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LAT, station.getLatitude());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LON, station.getLongitude());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LINE_TYPES, lineTypesToString(station));
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

    private String lineTypesToString(Stop s){
        ArrayList<Integer> lineTypes = s.getLineTypes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lineTypes.size(); i++){
            if(i == lineTypes.size() - 1){
                sb.append(lineTypes.get(i));
            }else{
                sb.append(lineTypes.get(i) + ",");
            }
        }
        return sb.toString();

    }

    private boolean shouldUpdate(long lastUpdate){
//        return true;
        long delta = System.currentTimeMillis() - lastUpdate;
        return (delta > Constants.DAY_IN_MILLISECONDS || lastUpdate == Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);
    }

}
