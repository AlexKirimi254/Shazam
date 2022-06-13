package com.example.shazam2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;


import com.example.shazam2.Shazam.Analysing.Comparer;
import com.example.shazam2.Shazam.Analysing.HashFile;
import com.example.shazam2.Shazam.Audio.ShzazamRecorder;
import com.example.shazam2.Shazam.DataBase.LoginData;
import com.example.shazam2.Shazam.DataBase.Processing.GetMusicList;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;


public class MainActivity extends AppCompatActivity {

    public Statement statement;
    public ShzazamRecorder record;

    boolean isConnected = false;
    boolean runIt = false;

    public static final int RequestPermissionCode = 1;


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                INTERNET);

        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO,INTERNET}, RequestPermissionCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tex = (TextView) findViewById(R.id.text);

    }


    public void analyse(View view){

        File directory = MainActivity.this.getFilesDir();
        File file = new File(directory, "recording.wav");

        if(checkPermission()) {
            TextView text = (TextView) findViewById(R.id.text);
            Button but = (Button) findViewById(R.id.button);

            RecordWavMaster RWM = new RecordWavMaster(directory.getPath());

            but.setEnabled(false);
            text.setText("Recording ...");
            try {
                file.createNewFile();

                RWM.recordWavStart();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        RWM.recordWavStop();
                        text.setText("Analyse");

                        but.setEnabled(true);

                        try {
                            record = new ShzazamRecorder();
                            record.listen(file.getPath());

                            DataBase base = new DataBase();
                            base.execute();

                        }catch (Exception err){
                            text.setText(err.getMessage());
                        }

                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 10000);



            }catch (FileNotFoundException e){
                text.setText("Error");
            } catch (IOException e) {
                text.setText("Error");
            } catch (Exception e) {
                text.setText("Error");
            }


        }else{
            requestPermission();
        }



    }

    class DataBase extends AsyncTask<Void, Void, Void> {

        String records = "",error="";
        String result;
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override

        protected Void doInBackground(Void... voids) {

            try
            {
                Connection connection = DriverManager.getConnection(LoginData.url, LoginData.username, LoginData.password);
                statement = connection.createStatement();
                Instant start = Instant.now();

                Comparer compare = new Comparer();
                compare.setRecordFile(record.getHashes());


                Statement state = connection.createStatement();
                GetMusicList list = new GetMusicList(state);
                int[] samplesFiles = list.musicIds();

                for (int file : samplesFiles) {

                    HashFile hfh = new HashFile(file, state);

                    compare.addMusic(hfh);

                }

                boolean success = false;
                result = compare.compare(true, state,start);


            }
            catch(Exception e)
            {
                error = e.toString();
            }

            return null;

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            TextView vie = (TextView) findViewById(R.id.text);
            if(error.equals("")){
                vie.setText(result);

            }else {
                vie.setText(error);
            }
            super.onPostExecute(aVoid);
        }
    }



}