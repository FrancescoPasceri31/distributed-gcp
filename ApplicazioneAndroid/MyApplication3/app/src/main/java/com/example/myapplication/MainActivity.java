package com.example.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements Serializable{

    final int REQUEST_PERMISSION_CODE = 1000;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer, mediaPlayer2;
    String pathSave=" ";
    boolean terminato=false;
    String pathFinale;
    long registrazioni=0;
    boolean creato=false;

    private void setupMediaRecorder(){
        mediaRecorder= new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioSamplingRate(8000);
        mediaRecorder.setOutputFile(pathSave);

    }

   // @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Richiesta permessi
        if(!checkPermissionFromDevice())
            requestPermission();

        final Button button = findViewById(R.id.button);
        final Spinner sp1 = findViewById(R.id.spinner);
        final Spinner sp2 = findViewById(R.id.spinner2);
        final ImageButton play = findViewById(R.id.imageButton);

        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Traduttore/";
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }
        pathSave = path + UUID.randomUUID().toString()+"_audio_record"+registrazioni+".3gp";

        button.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //deve registrare
                        // PRESSED


                        setupMediaRecorder();
                        try{
                            mediaRecorder.prepare();
                            mediaRecorder.start();

                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        Toast.makeText(MainActivity.this, "Recording ...", Toast.LENGTH_SHORT ).show();
                        terminato=false;
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASE
                        try{

                            mediaRecorder.stop();
                            mediaRecorder.release();

                            //ora dobbiamo creare la socket per comunicare con l'applicazione e farci ritornare un byte[] contenente il file audio e salvarlo
                            ThreadConnessione tc = new ThreadConnessione(MainActivity.this,pathSave, sp1, sp2);
                            tc.start();
                            //while(!terminato){
                            ;
                            //}

                            try {
                                Toast.makeText(MainActivity.this, "Processing...", Toast.LENGTH_LONG).show();
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!terminato)
                                Toast.makeText(MainActivity.this, "Servizio non disponibile", Toast.LENGTH_SHORT).show();
                            else {
                                File fileRegistrato = new File(pathSave);
                                fileRegistrato.delete();
                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(pathFinale);
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mediaPlayer.start();
                                mediaPlayer.setLooping(false);
                                setupMediaRecorder();

                                creato=true;

                                Toast.makeText(MainActivity.this, "Play", Toast.LENGTH_SHORT).show();
                            }
                            //registrazioni++;
                        }catch(RuntimeException e){
                            //Toast.makeText(MainActivity.this, "Tenere premuto ...", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this,"Tenere premuto ...", Toast.LENGTH_SHORT).show();
                        }

                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });


        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(creato) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(false);

                    Toast.makeText(MainActivity.this, "Play", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result== PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
