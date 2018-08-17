package in.ac.iiiitd.myapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity_A1_2016064 extends AppCompatActivity {
    protected final String StateTAG = "StateChange";

    protected boolean stopped = false;
    protected boolean paused = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String s1 = "Destroyed";
        if (stopped){
            s1 = "Stopped";
        }
        else if (paused){
            s1 = "Paused";
        }
        stopped = false;
        paused = false;
        logStateChangeString("Destroyed","Created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        String s1 = "Created";
        if (stopped) {
            s1 = "Stopped";
        }
        stopped = false;
        paused = false;
        logStateChangeString(s1,"Started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String s1 = "Started";
        if (paused){
            s1 = "Paused";
        }
        paused = false;
        stopped = false;
        logStateChangeString(s1,"Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        logStateChangeString("Resumed","Paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopped = true;
        logStateChangeString("Paused","Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logStateChangeString("Stopped","Destroyed");
    }

    protected void logStateChangeString(String state1, String state2){
        String s = "State of Activity: " + this.getClass().getSimpleName() +
                " changed from " + state1 + " to " + state2;
        Log.i(StateTAG,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        t.show();
    }
}
