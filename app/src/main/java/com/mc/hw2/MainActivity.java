package com.mc.hw2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements MusicItemFragment.OnListFragmentInteractionListener, MusicPlayerService.PlayerStatesListener, DownloaderService.DownloadStateListener {

    private TextView mTextMessage;
    private MusicItem currentlyPlaying = null;
    private MusicPlayerService playerService = null;
    private String TAG = this.getClass().getSimpleName();
    private MusicItemFragment musicItemFragment;




    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"Service Connected, playing the currentlyPlaying item");
            MusicPlayerService.PlayerBinder binder =
                    (MusicPlayerService.PlayerBinder) service;

            MainActivity.this.playerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.this.playerService = null;
        }
    };

    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
            }
            return false;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        /*BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);*/

        FragmentManager fmgr = getSupportFragmentManager();
//        musicItemFragment = MusicItemFragment.newInstance(1);
        musicItemFragment = (MusicItemFragment) fmgr.findFragmentById(R.id.list_fragment);
        /*FragmentTransaction transaction = fmgr.beginTransaction();

        transaction.add(R.id.frame_container,musicItemFragment);
        transaction.commit();*/
    }

    @Override
    protected void onStart() {
        Intent i = new Intent(this, MusicPlayerService.class);
        if (!MusicPlayerService.isRunning){
            Log.i(TAG,"onStart: Service not running, starting service");
            startService(i);
        }
        Log.i(TAG,"onStart: binding service");
        this.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (currentlyPlaying == null) {
            Intent i = new Intent(this, MusicPlayerService.class);
            stopService(i);
        }
        super.onDestroy();
    }

    @Override
    public void onListFragmentItemClick(MusicItem item) {
        //TODO: Check playing and start/edit music player service.
        Log.d(TAG,"onListFragmentItemClick : item = " + item);

        if (item.type == MusicItem.TYPE_ONLINE){
            //Download the file.
            DownloaderService.startDownload(this, item.path, "sampleFile.mp3", this);
            Toast.makeText(this, "Starting Download", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, MusicPlayerService.class);
        if (!MusicPlayerService.isRunning){
            Log.i(TAG,"Service not running, starting service");
            startService(i);
        }
        if (playerService==null){
            Log.i(TAG,"Service not bound, binding service");
            this.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        }


        if (item!=currentlyPlaying) {
            Log.i(TAG,"item diff from currentlyPlaying, playing this item");
            currentlyPlaying = item;
            try {
                playerService.playItem(item, this);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unable to play the currentlyPlaying item: " + item);
            }
        }
        else{
            Log.i(TAG,"onItemClick: item passed is same as currentlyPlaying, must be playing. Pausing the service");
            currentlyPlaying = null;
            playerService.pause(this);
            musicItemFragment.mAdapter.updatePlayingItem(null);
        }

    }

    @Override
    public MusicItem itemPlaying() {
        return currentlyPlaying;
    }

    @Override
    public void onPlaying(MusicItem item) {
        currentlyPlaying = item;
        musicItemFragment.mAdapter.updatePlayingItem(item);
    }

    @Override
    public void onPaused() {
        currentlyPlaying = null;
        musicItemFragment.mAdapter.updatePlayingItem(null);
    }

    @Override
    public void onCompleted(String url, String filename) {
        if (filename.equals("sampleFile.mp3")){
            //Sample file has been downloaded.
            Log.d(TAG,"Sample File downloaded");
            MusicItem item = new MusicItem("Sample File", MusicItem.TYPE_ONLINE, "http://faculty.iiitd.ac.in/~mukulika/s1.mp3");
            musicItemFragment.mAdapter.onOnlineFileDownloaded(this,item);
        }
    }
}
