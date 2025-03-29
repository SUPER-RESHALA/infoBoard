package com.simurg.infoboard;

import android.Manifest;
import android.app.Activity;
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

import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.json.JSONHandler;
import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.player.MediaPlayerManager;
import com.simurg.infoboard.utils.mapUtils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> permissionLauncher;

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



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.black));
//            window.setNavigationBarColor(Color.BLACK);
//        }




        File baseDir= this.getExternalFilesDir(null);
//        File appDir= new File(baseDir,"Files");
//        if (!appDir.mkdir()){
//            Log.e("mkDir", "Cant mkDir");
//        }

        FileLogger.init(this);
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
        VideoView videoView= findViewById(R.id.videoView);
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView= findViewById(R.id.textView);
        MediaPlayerManager mp=new MediaPlayerManager(textView,imageView,videoView);
       // List<MediaItem> mediaItems=mp.createMediaList(files);

//mp.setPlaylist(files);
      //mp.play();
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String path = downloadsDir.getAbsolutePath(); // Вернёт /storage/emulated/0/Download

        try {
            List<Map<String, Object>> mylist= JSONHandler.readJsonFromFile(new File(path+"/file.json"));
            for (Map<String, Object> map:mylist){
                if (map.containsKey("duration")){
                    System.err.println("--------------------------------------"+ map.get(MediaItem.durationStr).getClass());
                    if ( map.get(MediaItem.durationStr) instanceof String){
                        System.out.println("IT STRING ++++++++++++++++++");
                    }
                }

                Log.i(" map", mapUtils.stringValueOfMap(map)+"\n"+"\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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