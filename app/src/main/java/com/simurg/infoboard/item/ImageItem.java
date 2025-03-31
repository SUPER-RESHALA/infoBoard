package com.simurg.infoboard.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;
import java.util.Date;

public class ImageItem extends MediaItem{
    public static final String TAG= "ImageItem";
    protected int duration;
    public ImageItem(File file, int duration) {
        super(file);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }
    public ImageItem(String name,int duration) {
       super(name);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }
    public ImageItem(String name, int duration, Date scheduledTime, Boolean isScheduled) {
        super(name,scheduledTime,isScheduled);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }
    public ImageItem(File file, int duration, Date scheduledTime, Boolean isScheduled) {
        super(file,scheduledTime,isScheduled);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int getType() {
        return TYPE_IMAGE;
    }
    public void setImage( ImageView imageView){
        if (file.exists()){
            Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
imageView.setImageBitmap(bitmap);
        }else {
            FileLogger.logError(TAG,"File not exist");
        }

    }
    @Override
    public void play(MediaPlayerManager mp) {
        FileLogger.log("TAG","SetImage call");
    setImage(mp.getImageView());
    mp.hideTextView();
    mp.hideVideoView();
    mp.showImageView();
    FileLogger.log(TAG,"Stop HAndler Called");
    mp.stopHandler();
    FileLogger.log(TAG," Call PostDelayed");
    mp.getHandler().postDelayed(mp::playNext, duration * 1000);
    }
}
