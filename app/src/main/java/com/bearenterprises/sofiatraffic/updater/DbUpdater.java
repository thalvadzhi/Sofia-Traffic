package com.bearenterprises.sofiatraffic.updater;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;

import static com.bearenterprises.sofiatraffic.utilities.Utility.toastOnUiThread;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.cloudBackedSharedPreferences.CloudBackedSharedPreferences;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.utilities.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.DbManipulator;
import com.bearenterprises.sofiatraffic.utilities.FileDownloader;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.XMLCoordinateParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by thalv on 08-Jul-16.
 */
public class DbUpdater extends AsyncTask{
    private Context context;
    private boolean fileDownloaderExceptionHappened;
    private CoordinatorLayout coordinatorLayout;

    public DbUpdater(Context context) {
        this.context = context;
        this.coordinatorLayout = ((MainActivity)this.context).getCoordinatorLayout();
        this.fileDownloaderExceptionHappened = false;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        SharedPreferences preferences = this.context.getSharedPreferences(Constants.SHARED_PREFERENCES_LAST_UPDATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        long lastUpdate = preferences.getLong(Constants.KEY_LAST_UPDATE, Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);


        if (shouldUpdate(lastUpdate)){
            //means it's time to update
            try {
                update();
                editor.putLong(Constants.KEY_LAST_UPDATE, System.currentTimeMillis());
                editor.commit();
                Utility.makeSnackbar("Информацията за спирките беше обновена!", coordinatorLayout);
            } catch (Exception e) {
                Utility.makeSnackbar("Информацията за спирките НЕ беше обновена :(", coordinatorLayout);
            }

        }

        return null;
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
        FileDownloader downloader = new FileDownloader(this.context, Constants.XML_DOWNLOAD_URL, Constants.XML_COORDINATE_FILE, notifier);
        downloader.run();

        if (fileDownloaderExceptionHappened == true){
            toastOnUiThread("Couldn't download station info. There's no internet connection :(", this.context);
        }


        ArrayList<Station> stations = null;
        try {
            stations = XMLCoordinateParser.parse(this.context, Constants.XML_COORDINATE_FILE);
        } catch (ParserConfigurationException e) {
            Log.d("Parse error", "Couldn't parse coordinates file", e);
            throw new Exception(e);
        } catch (SAXException e) {
            Log.d("XML read error", "Couldn't read xml", e);
            throw new Exception(e);
        } catch (IOException e) {
            Log.d("File doesn't exist", "XML Coordinates file doesn't exist", e);
            throw new Exception(e);
        }
        ArrayList<ContentValues> stationInformation = new ArrayList<>();
        for (Station station : stations) {
            ContentValues v = new ContentValues();
            v.put(DbHelper.FeedEntry.COLUMN_NAME_CODE, station.getCode());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_STATION_NAME, station.getName());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LAT, station.getLatitude());
            v.put(DbHelper.FeedEntry.COLUMN_NAME_LON, station.getLongtitute());
            stationInformation.add(v);
        }


        DbManipulator manipulator = new DbManipulator(this.context);
        manipulator.deleteAll();
        manipulator.insert(stationInformation);
        manipulator.closeDb();

    }

    private boolean shouldUpdate(long lastUpdate){
        long delta = System.currentTimeMillis() - lastUpdate;
        return (delta > Constants.WEEK_IN_MILLISECONDS || lastUpdate == Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);
    }

}
