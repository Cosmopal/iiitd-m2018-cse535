package com.mc.hw3.quizapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * An activity representing a list of QuestionsList. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link QuestionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class QuestionListActivity extends AppCompatActivity implements IPDialogFragment.IpDialogClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private QuestionsAdapter adapter;
    IPDialogFragment dialogFragment;
    private String serverIp = "http://192.168.59.30/phpupload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                dialogFragment = new IPDialogFragment();
                dialogFragment.listener = QuestionListActivity.this;
                dialogFragment.show(getFragmentManager(), "ipAddressDialogFragment");
            }
        });

        if (findViewById(R.id.question_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.question_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    public void submit() {
        //TODO: Submit list to server.
        Toast.makeText(this, "TODO: Upload files", Toast.LENGTH_SHORT).show();
        createCsv();
        UploadTask task = new UploadTask();
        task.execute(serverIp);

    }

    public void createCsv() {
//        File file = new File(this.getFilesDir(), "answers.csv");
        FileOutputStream outputStream = null;

        try {
            outputStream = openFileOutput("answers.csv", Context.MODE_PRIVATE);
            ArrayList<Question> questions = new QuestionsDBHelper(getApplicationContext()).getAllQuestions();
            for (Question q : questions) {
                outputStream.write(q.getCsvString().getBytes());
            }
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new QuestionsAdapter(this, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPositiveClick(String ip) {
        this.serverIp = ip;
        submit();

    }

    @Override
    public String getIpAddress() {
        return serverIp;
    }

    class UploadTask extends AsyncTask<String, Float ,String>{

        private String TAG = "UploadTask";

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            File file = new File(getApplicationContext().getFilesDir(), "answers.csv");

            if (!file.isFile()){
                //Error reading file
                Log.e(TAG, "There was some error reading the files");
                return "Error reading Submission File";
            }

            //Check Network connection

            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!manager.getActiveNetworkInfo().isConnected()){
                Log.w(TAG, "Network not connected, exiting");
                return "Network not Connected";
            }

            try{
                int response = uploadFile(file, urlString);
                if (response == 200){
                    //Upload was successful
                    return "Upload Successful";
                }
                else{
                    return "Upload unsuccessful, check logs";
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "Error: Submission File Not Found";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "URL " + urlString + " is invalid";
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
//            return "Upload Successful";
        }

        protected int uploadFile(File file, String urlString) throws IOException {
            String lineEnd = "\r\n";
            String boundary = "***thisIsBoundary***";


            FileInputStream inputStream = new FileInputStream(file);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("fileToUpload", file.getName());

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            outputStream.writeBytes("--" + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\"" +
                    ";filename=\"" + file.getName() + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            int maxBuff = 1024 * 1024;
            int bufSize = Math.min(maxBuff, inputStream.available());
            byte[] buffer = new byte[bufSize];
            int numBytesRead = inputStream.read(buffer, 0, bufSize);
            int count = 0;
            while (numBytesRead > 0){
                count++;
                outputStream.write(buffer);
                bufSize = Math.min(maxBuff, inputStream.available());
                numBytesRead = inputStream.read(buffer, 0, bufSize);
            }
            Log.d(TAG, "Uploaded in " + count + " buffers");

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("--" + boundary + "--" + lineEnd);


            int response = connection.getResponseCode();
            String responseMsg = connection.getResponseMessage();
            Log.i(TAG,"uploadFile: server response code = " + response + " " +
                    "\n message  = " + responseMsg);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(QuestionListActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

}
