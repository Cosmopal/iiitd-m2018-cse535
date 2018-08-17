package in.ac.iiiitd.myapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity_A1_2016064 extends BaseActivity_A1_2016064 {
    protected final String StateTAG = "StateChange";

    protected boolean restarted = false;
    protected boolean paused = true;

    EditText etv_name;
    EditText etv_rollno;
    EditText etv_branch;
    EditText etv_course1;
    EditText etv_course2;
    EditText etv_course3;
    EditText etv_course4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etv_name = findViewById(R.id.et_name);
        etv_rollno = findViewById(R.id.et_rollno);
        etv_branch = findViewById(R.id.et_branch);
        etv_course1 = findViewById(R.id.et_course1);
        etv_course2 = findViewById(R.id.et_course2);
        etv_course3 = findViewById(R.id.et_course3);
        etv_course4 = findViewById(R.id.et_course4);

    }

    public void submit(View v){
        final Intent i = new Intent(this,DisplayActivity_A1_2016064.class);
        i.putExtra("name",etv_name.getText().toString());
        i.putExtra("rollno",etv_rollno.getText().toString());
        i.putExtra("branch",etv_branch.getText().toString());
        i.putExtra("course1",etv_course1.getText().toString());
        i.putExtra("course2",etv_course2.getText().toString());
        i.putExtra("course3",etv_course3.getText().toString());
        i.putExtra("course4",etv_course4.getText().toString());
        startActivity(i);

    }

    public void clear(View v){
        etv_name.setText("");
        etv_rollno.setText("");
        etv_branch.setText("");
        etv_course4.setText("");
        etv_course3.setText("");
        etv_course2.setText("");
        etv_course1.setText("");
    }
}
