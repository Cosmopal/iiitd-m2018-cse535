package com.mc.hw2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    public PlayerBinder binder = new PlayerBinder();
    private MediaPlayer mediaPlayer;
    private final String CHANNEL_ID = "playerChannel";
    private final String TAG = "PlayerService";
    private NotificationCompat.Builder mPlayingNotificationBuilder;
    private final int NOTIF_ID = 1;
    private static final String RES_ID = "res_id";
    private MusicItem currentlyPlaying = null;
    private final String RES_PREFIX = "android.resource://" + "com.mc.hw2" + "/";
    public static boolean isRunning = false;

    public MusicPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Create ongoing notification:

        //Create pending intent for tap action

        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);


        createNotificationChannel();
        mPlayingNotificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Playing Music")
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        //Will use this notif when playing music.


        //Initialising player and other variables from intent.

        /*int resID = intent.getIntExtra(RES_ID, 0);
        if (resID != 0){
            MusicItem item = new MusicItem("Not Available", MusicItem.TYPE_RES, resID);
            prepMediaPlayer(item);
        }*/
        isRunning = true;

        return START_STICKY;

//        return super.onStartCommand(intent, flags, startId);
    }

    private void prepMediaPlayer(MusicItem item, PlayerStatesListener listener) throws IOException {
        String uriPath = "";
        if (item.type == MusicItem.TYPE_RES){
            uriPath = RES_PREFIX + item.resID;
        }
        else{
            uriPath = item.path;
        }
        Log.d(TAG,"uriPath = " + uriPath);
        Uri uri = Uri.parse(uriPath);
        if (mediaPlayer ==null){
            Log.i(TAG,"prepMediaPlayer: mediaPlayer was null, creating player");
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();
        }
        else {
            if (!currentlyPlaying.equals(item)) {
                Log.i(TAG,"A different song is loaded into the player, resetting");
                mediaPlayer.pause();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, uri);

            /*
            mediaPlayer.create already calls prepare();
             */
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            }
            /*if (mediaPlayer.isPlaying()) {
                Log.i(TAG, "prepMediaPlayer: Player is already playing, stopping player");
                mediaPlayer.pause();
            }*/
        }
        currentlyPlaying = item;
        if (listener != null){
            listener.onPlaying(item);
        }
    }

    private void showNotification() {
        NotificationManagerCompat mgr = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = mPlayingNotificationBuilder
                .setContentText(currentlyPlaying.name);
        mgr.notify(NOTIF_ID, builder.build());
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Player Notifications Channel";
            String desc = "This channel delivers notifications from the music player service";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            else{
                Log.e(TAG,"notificationManager returned is null");
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG,"onPrepared called");
        mediaPlayer.start();
    }

    public class PlayerBinder extends Binder{
        MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }

    /*
    Public methods for the parent activity.
    Include media playback controls etc.
     */

    public void playItem(MusicItem item, PlayerStatesListener listener) throws IOException {
        if (currentlyPlaying==null || !currentlyPlaying.equals(item)) {
            prepMediaPlayer(item, listener);
        }
        else{
            Log.i(TAG,"playItem: item passed is same as currently Playing, should be already loaded in mediaPlayer. Playing the file");
            mediaPlayer.start();
        }
    }

    public void pause(PlayerStatesListener listener){
        mediaPlayer.pause();
        listener.onPaused();

    }

    public void stop(){
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
//        throw new UnsupportedOperationException("Not yet implemented");
    }


    interface PlayerStatesListener{
        void onPlaying(MusicItem item);
        void onPaused();
    }
}
