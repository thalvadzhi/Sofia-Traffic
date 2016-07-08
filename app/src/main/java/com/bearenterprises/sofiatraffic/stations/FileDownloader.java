package com.bearenterprises.sofiatraffic.stations;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.util.Log;

public class FileDownloader implements Runnable {
    public static final String HEADER_ETAG = "etag";

    private static final String TAG = "FileDownloader";

    private static final int DOWNLOAD_BUFFER_SIZE = 4096;
    private final Context context;
    private final String downloadUrl;
    private final String filename;
    private String tag;
    private ExceptionInFileDownloaderNotifier exceptionInFileDownloaderNotifier;

    public FileDownloader(Context context, String downloadUrl, String filename, ExceptionInFileDownloaderNotifier notifier) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.exceptionInFileDownloaderNotifier = notifier;
    }

    public static abstract class ExceptionInFileDownloaderNotifier{
        public abstract void notifyExceptionHappened();
    }
    public void run() {

        try {
            final URLConnection conn = new URL(downloadUrl).openConnection();
            conn.setUseCaches(false);

            Log.i(TAG, "Download Started: " + downloadUrl);

            tag = conn.getHeaderField(HEADER_ETAG);
            final BufferedInputStream inStream = new BufferedInputStream(conn.getInputStream());
            final File outFile = new File(context.getFilesDir() + File.separator + filename);
            final FileOutputStream fileStream = new FileOutputStream(outFile);
            final BufferedOutputStream outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
            final byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            int bytesRead = 0;
            while ((bytesRead = inStream.read(data, 0, data.length)) >= 0) {
                outStream.write(data, 0, bytesRead);
            }

            outStream.close();
            fileStream.close();
            inStream.close();

            Log.i(TAG, "Download finished: " + downloadUrl);
            Log.i(TAG, "File is stored in direcotry:" + outFile.getAbsolutePath().toString());
        } catch (Exception e) {
            Log.e(TAG, "Error while downloading " + downloadUrl, e);
            this.exceptionInFileDownloaderNotifier.notifyExceptionHappened();

            tag = null;
        }
    }
    public String getTag() {
        return tag;
    }
}