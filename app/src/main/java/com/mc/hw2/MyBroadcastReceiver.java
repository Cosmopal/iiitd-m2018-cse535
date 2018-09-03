package com.mc.hw2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BrdcastRcvr";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = "";
        switch (intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
                action = "BOOT_COMPLETED";
                break;
            case Intent.ACTION_POWER_CONNECTED:
                action = "POWER_CONNECTED";
                break;
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                action = "AIRPLANE_MODE_CHANGED";
                break;
        }

        Log.i(TAG,"Received "+ action + " broadcast" );
        Toast.makeText(context, action + " received", Toast.LENGTH_SHORT).show();

    }
}
