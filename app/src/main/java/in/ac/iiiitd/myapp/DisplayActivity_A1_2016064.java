package in.ac.iiiitd.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayActivity_A1_2016064 extends BaseActivity_A1_2016064 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent i = getIntent();
        ((TextView)findViewById(R.id.tv_name)).setText(i.getStringExtra("name"));
        ((TextView)findViewById(R.id.tv_rollno)).setText(i.getStringExtra("rollno"));
        ((TextView)findViewById(R.id.tv_branch)).setText(i.getStringExtra("branch"));
        ((TextView)findViewById(R.id.tv_course1)).setText(i.getStringExtra("course1"));
        ((TextView)findViewById(R.id.tv_course2)).setText(i.getStringExtra("course2"));
        ((TextView)findViewById(R.id.tv_course3)).setText(i.getStringExtra("course3"));
        ((TextView)findViewById(R.id.tv_course4)).setText(i.getStringExtra("course4"));
    }
}
