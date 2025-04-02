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
import com.simurg.infoboard.ftp.FtpConnectionManager;
import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.item.MediaItemHandler;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
     // File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
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

//        JsonObj jsonObj= new JsonObj(new File(baseDir,config.getJsonName()));
//        if (jsonObj.getFile().exists()){
//            try {
//                JSONHandler.readJsonFromFile(jsonObj.getFile());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
Log.i("ConfigValues", config.getHost()+"\n"+ config.getUserName()+"\n"+config.getPassword()+"\n"
        +config.getMediaDirName()+"\n"+ config.getJsonName()+"\n"+config.getId());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.black));
//            window.setNavigationBarColor(Color.BLACK);
//        }

//        System.out.println(MediaItemHandler.validateScheduledTime("07:33")
//                +"\n");
//
//        System.out.println(MediaItemHandler.validateScheduledTime("16:43")
//                +"\n");
//
//        System.out.println(MediaItemHandler.validateScheduledTime("00:03")
//                +"\n");
//
//        System.out.println(MediaItemHandler.validateScheduledTime("24:33")
//                +"\n");
//
//        System.out.println(MediaItemHandler.validateScheduledTime("09099090")
//                +"\n");
//        System.out.println("------------\n\n\n----------");
//        System.out.println(CustomDate.getCurrentDate()
//                +"\n");
//        System.out.println(CustomDate.scheduledTimeToCurrentDate(MediaItemHandler.validateScheduledTime("16:43"))
//                +"\n");


//        File appDir= new File(baseDir,"Files");
//        if (!appDir.mkdir()){
//            Log.e("mkDir", "Cant mkDir");
//        }


        File [] files= baseDir.listFiles();
//System.err.println(FileLogger.getLogFilePath());
//File logFile= new File(FileLogger.getLogFilePath());
//if (logFile.exists()){
//    System.out.println("EXIIIIIIIIIIIIIIIIIIIIST");
//}else{
//    Log.e("LOOOOOOOOOOG", "NOT NOt NOT NOT NOT");
//        }
//        try {
//            Scanner scanner = new Scanner(logFile);
//            Log.e("","------------------------");
//            while (scanner.hasNextLine()) {
//                System.out.println(scanner.nextLine());
//            }
//            Log.e("","------------------------end");
//            scanner.close();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        MediaPlayerManager mp=new MediaPlayerManager(textView,imageView,videoView);
       // List<MediaItem> mediaItems=mp.createMediaList(files);

//mp.setPlaylist(files);
      //mp.play();
         // Вернёт /storage/emulated/0/Download

//        try {
//            List<Map<String, Object>> mylist= JSONHandler.readJsonFromFile(new File(path+"/file.json"));
//            for (Map<String, Object> map:mylist){
//                if (map.containsKey("duration")){
//                    System.err.println("--------------------------------------"+ map.get(MediaItem.durationStr).getClass());
//                    if ( map.get(MediaItem.durationStr) instanceof String){
//                        System.out.println("IT STRING ++++++++++++++++++");
//                    }
//                }
//
//                Log.i(" map", mapUtils.stringValueOfMap(map)+"\n"+"\n");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }






//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String hostname = "ftp.simurg.by";
//                String user = "timetracker@timetracker.simurg-mp.com";
//                String password = "TimetrackerAdmin";
//                FtpConnectionManager ftpConnectionManager = new FtpConnectionManager();
//                ftpConnectionManager.connect(hostname);
//                ftpConnectionManager.login(user, password);
//FTPClient ftpClient =ftpConnectionManager.getFtpClient();
//// Запрашиваем список поддерживаемых команд
//                try {
//                    ftpClient.sendCommand("FEAT");
//                    String reply = ftpClient.getReplyString();
//                    System.out.println(reply);
//                    ftpConnectionManager.logout();
//                    ftpConnectionManager.disconnect();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//// Читаем ответ сервера
//            }
//        }).start();


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