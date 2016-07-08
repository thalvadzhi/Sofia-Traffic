package com.bearenterprises.sofiatraffic.updater;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import static com.bearenterprises.sofiatraffic.utilities.Utility.toastOnUiThread;

import com.bearenterprises.sofiatraffic.Constants.Constants;
import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.stations.DbHelper;
import com.bearenterprises.sofiatraffic.stations.DbManipulator;
import com.bearenterprises.sofiatraffic.stations.FileDownloader;
import com.bearenterprises.sofiatraffic.stations.Station;
import com.bearenterprises.sofiatraffic.stations.XMLCoordinateParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by thalv on 08-Jul-16.
 */
public class DbUpdater extends Thread{
    private Context context;
    private boolean fileDownloaderExceptionHappened;

    public DbUpdater(Context context) {
        this.context = context;
        this.fileDownloaderExceptionHappened = false;
    }



    public class ExceptionNotifier extends FileDownloader.ExceptionInFileDownloaderNotifier{

        @Override
        public void notifyExceptionHappened() {
            fileDownloaderExceptionHappened = true;
        }
    }


    public void update(){
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
            toastOnUiThread("Couldn't update coordinates file", this.context);
            return;
        } catch (SAXException e) {
            Log.d("XML read error", "Couldn't read xml", e);
            toastOnUiThread("Couldn't update coordinates file", this.context);
            return;
        } catch (IOException e) {
            Log.d("File doesn't exist", "XML Coordinates file doesn't exist", e);
            toastOnUiThread("Couldn't update coordinates file", this.context);
            return;
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
        manipulator.insert(stationInformation);

    }

    private boolean shouldUpdate(long lastUpdate){
        long delta = System.currentTimeMillis() - lastUpdate;
        return (delta > Constants.WEEK_IN_MILLISECONDS || lastUpdate == Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);
    }
    @Override
    public void run() {
        SharedPreferences preferences = this.context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        long lastUpdate = preferences.getLong(Constants.SHARED_PREFERENCES_DATE_LAST_UPDATE, Constants.SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME);


        if (shouldUpdate(lastUpdate)){
            //means it's time to update
            update();
            editor.putLong(Constants.SHARED_PREFERENCES_DATE_LAST_UPDATE, System.currentTimeMillis());
            editor.commit();
            toastOnUiThread("Station info updated successfully!", this.context);
        }

    }
}
