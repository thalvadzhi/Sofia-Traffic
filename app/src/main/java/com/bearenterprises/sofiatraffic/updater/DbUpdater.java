package com.bearenterprises.sofiatraffic.updater;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;

import static com.bearenterprises.sofiatraffic.utilities.Utility.toastOnUiThread;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.DescriptionsParser;
import com.bearenterprises.sofiatraffic.utilities.FileDownloader;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.utilities.JSONParser;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.XMLCoordinateParser;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by thalv on 08-Jul-16.
 */
public class DbUpdater extends AsyncTask<Void, String, Void>{
    private Context context;
    private boolean fileDownloaderExceptionHappened;
    private CoordinatorLayout coordinatorLayout;

    public DbUpdater(Context context) {
        this.context = context;
        this.coordinatorLayout = ((MainActivity)this.context).getCoordinatorLayout();
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
                publishProgress(Constants.SHOW_DIALOG);
                update();
                editor.putLong(Constants.KEY_LAST_UPDATE, System.currentTimeMillis());
                editor.commit();
                Utility.makeSnackbar("Информацията за спирките беше обновена!", coordinatorLayout);
            } catch (Exception e) {
                Utility.makeSnackbar("Информацията за спирките НЕ беше обновена :(", coordinatorLayout);
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


    public class ExceptionNotifier extends FileDownloader.ExceptionInFileDownloaderNotifier{

        @Override
        public void notifyExceptionHappened() {
            fileDownloaderExceptionHappened = true;
        }
    }


    public void update() throws Exception{
        ExceptionNotifier notifier = new ExceptionNotifier();
        //unfortunately FileDownloader is a thread - so no exceptions can be carried to caller thread
        FileDownloader downloader = new FileDownloader(this.context, Constants.COORDINATES_DOWNLOAD_URL_JSON, Constants.JSON_COORDINATE_FILE, notifier);
        downloader.run();

        FileDownloader downloaderDescriptions = new FileDownloader(this.context, Constants.DESCRIPTIONS_DOWNLOAD_URL, Constants.DESCRIPTIONS_FILE_NAME, notifier);
        downloaderDescriptions.run();

        if (fileDownloaderExceptionHappened == true){
            ((MainActivity)context).makeSnackbar("Няма връзка с интернет :(");
            throw new Exception("No internet connection");
        }

        Map<String, String> descriptions = DescriptionsParser.parse(this.context, Constants.DESCRIPTIONS_FILE_NAME);

        ArrayList<Station> stations = null;
        stations = JSONParser.getStationsFromFile(Constants.JSON_COORDINATE_FILE, this.context);
        ArrayList<ContentValues> stationInformation = new ArrayList<>();
        for (Station station : stations) {
            ContentValues v = new ContentValues();
            String description = descriptions.get(station.getCode());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_CODE, station.getCode());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME, station.getName());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LAT, station.getLatitude());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LON, station.getLongtitute());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_DESCRIPTION, description);
            stationInformation.add(v);
        }


        DbManipulator manipulator = new DbManipulator(this.context);
        manipulator.deleteAll();
        manipulator.insert(stationInformation);
        manipulator.closeDb();

    }

    private boolean shouldUpdate(long lastUpdate){
//        return true;
        long delta = System.currentTimeMillis() - lastUpdate;
        return (delta > Constants.WEEK_IN_MILLISECONDS || lastUpdate == Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);
    }

}
