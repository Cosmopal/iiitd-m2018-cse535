package com.mc.hw2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MusicPlayerService extends Service {
    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
