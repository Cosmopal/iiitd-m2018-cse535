package com.mc.hw2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloaderService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.mc.hw2.action.DOWNLOAD";

    private static final String PARAM_URL = "com.mc.hw2.extra.PARAM1";
    private static final String PARAM_FILENAME = "com.mc.hw2.extra.PARAM2";
    private static final String TAG = "DownloadService";
    private static DownloadStateListener mListener;

    private Handler mHandler;


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG,"handleMessage");
                Log.i(TAG,msg.getData().getString("url", "none"));
                if (!msg.getData().getString("url", "none").equals("none")){
                    //Download successful
                    Log.d(TAG,"successful download");
                    mListener.onCompleted(
                            msg.getData().getString("url"),
                            msg.getData().getString("filename")
                    );
                }

                super.handleMessage(msg);
            }
        };
        return super.onStartCommand(intent, flags, startId);
    }

    public DownloaderService() {
        super("DownloaderService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startDownload(Context context, String url, String filename, DownloadStateListener listener) {
        Log.d(TAG,"startDownload");
        Intent intent = new Intent(context, DownloaderService.class);
//        intent.setAction(ACTION_FOO);
        intent.putExtra(PARAM_URL, url);
        intent.putExtra(PARAM_FILENAME, filename);
        mListener = listener;

        Bundle bundle = new Bundle();
        context.startService(intent);

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String url = intent.getStringExtra(PARAM_URL);
            final String filename = intent.getStringExtra(PARAM_FILENAME);
            try {
                Log.i(TAG,"onHandleIntent: handleDownloadaction");
                handleDownloadAction(url, filename);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onHandleIntent: Error handling download action, maybe in the final block");
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleDownloadAction(String fileUrl, String filename) throws IOException {

        Log.i(TAG,"handleDownloadAction, fileUrl = " + fileUrl + " filename = " + filename);
        ShowToast toast = new ShowToast(this);
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()){
            Log.i(TAG,"Network not available");
            toast.duration = Toast.LENGTH_LONG;
            toast.text = "Network not available";
            mHandler.post(toast);
            return;
        }
        Log.i(TAG,"Network is available, proceeding for download");

        FileOutputStream outputStream = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(fileUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            int response = urlConnection.getResponseCode();
            if (response!=HttpURLConnection.HTTP_OK){
                Log.e(TAG,"Error response code from urlconnection, error code = " + response);
                toast.text = "Error code received = " + response;
                mHandler.post(toast);
                return;
            }
            Log.i(TAG,"Connection successful, proceeding to download file");
            in = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int currentBuffLen = 0;
            int totalDownloadSize = 0;

            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            while ((currentBuffLen = in.read(buffer)) >0){
                outputStream.write(buffer);
                totalDownloadSize += currentBuffLen;
                Log.v(TAG,"received buffer");

            }
            Log.i(TAG,"Download Successful, Total download size = " + totalDownloadSize);
//            outputStream.close();

            toast.text = "File Downloaded Successfully";
            mHandler.post(toast);
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("url",fileUrl);
            bundle.putString("filename",filename);
            message.setData(bundle);
            mHandler.sendMessage(message);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG,"Error creating file");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,"The url - " + fileUrl + " is incorrect");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (outputStream!=null){
                outputStream.close();
            }
            if (in!=null){
                in.close();
            }
            if (urlConnection!=null){
                urlConnection.disconnect();
            }
        }
    }

    class ShowToast implements Runnable{
        public String text;
        public Context context;
        public int duration = Toast.LENGTH_SHORT;

        public ShowToast(Context context) {
            this.context = context;
        }

        public ShowToast(Context context, String text, int duration){
            this.context = context;
            this. text = text;
            if (duration==Toast.LENGTH_LONG) {
                this.duration = duration;
            }
        }

        @Override
        public void run() {
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }

    interface DownloadStateListener{
        void onCompleted(String url, String filename);
    }
}
