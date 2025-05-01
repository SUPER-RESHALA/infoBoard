package com.simurg.infoboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simurg.infoboard.config.Config;
import com.simurg.infoboard.file.FileHandler;
import com.simurg.infoboard.file.FileSyncService;
import com.simurg.infoboard.ftp.FtpConnectionManager;
import com.simurg.infoboard.ftp.FtpFileManager;
import com.simurg.infoboard.item.ImageItem;
import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.item.MediaItemHandler;
import com.simurg.infoboard.item.VideoItem;
import com.simurg.infoboard.json.JSONHandler;
import com.simurg.infoboard.json.JsonObj;
import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.mydate.CustomDate;
import com.simurg.infoboard.player.MediaPlayerManager;
import com.simurg.infoboard.utils.mapUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> permissionLauncher;
    private String prefsName="myPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {

                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        String permission = entry.getKey();
                        boolean isGranted = entry.getValue();
                        if (isGranted) {
                            Log.i("Permissions", "Разрешение предоставлено: " + permission);
                        } else {
                            Log.e("Permissions", "Разрешение отклонено: " + permission);
                            finish();
                        }
                    }
                }
        );
        requestPermissions();
        setUiOptions();
        File baseDir= this.getExternalFilesDir(null);
 //   File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String path = downloadsDir.getAbsolutePath();
        Config config= new Config(new File( baseDir,"config.json"));
        try {
            Map<String,String> configMap= config.getAllConfigValues();
           boolean success= config.setupConfig(configMap);
           if (!success)throw new IOException("Config setup failed");
        } catch (IOException e) {
            TextView textView= findViewById(R.id.textView);
            String errorMessage=getString(R.string.configErrMessage1)+" "+baseDir.getAbsolutePath()+"  "+getString(R.string.configErrMessage2)+"\n"+getString(R.string.configGuide);
            textView.setText(errorMessage);
            textView.setVisibility(View.VISIBLE);
            Log.e("Config", "ErrorConfig Setup");
            Handler handler= new Handler();
            handler.postDelayed(()->{
                finish();
                System.exit(0);
            },30000);
            return;
        }
        SharedPreferences prefs= getApplicationContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("id",config.getId());
        editor.commit();
        FileLogger.init(this, prefsName);
        VideoView videoView= findViewById(R.id.videoView);
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView= findViewById(R.id.textView);
MediaPlayerManager mp = new MediaPlayerManager(textView,imageView,videoView);
File jsonFile= new File(baseDir, config.getJsonName());
//Log.i("deleteAllTmp", "success is: "+FileSyncService.deleteAllTmp(baseDir,".tmp"));
//        try {
//            FileSyncService.startPlaylistNoDownload(jsonFile,baseDir,mp,this);
//        } catch (IOException e) {
//          Log.e("MAINERROR", e.getMessage()+"   "+Log.getStackTraceString(e));
//        }

        FtpConnectionManager ftpConnectionManager= new FtpConnectionManager();
        FtpFileManager f=new FtpFileManager(ftpConnectionManager.getFtpClient());

        //TODO Clear unused file(old media)|DONE|& Create method startPlaylist when app start|Done no testing| & add exception handle(delete tmp when download)& create method when all playlist files ==null (endless cycle)|Done| not testing
//todo begin

Log.i("ConfigValues", config.getHost()+"\n"+ config.getUserName()+"\n"+config.getPassword()+"\n"
        +config.getMediaDirName()+"\n"+ config.getJsonName()+"\n"+config.getId());

    }//ON CREATE
    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        };
        permissionLauncher.launch(permissions);
    }
    private void setUiOptions(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}