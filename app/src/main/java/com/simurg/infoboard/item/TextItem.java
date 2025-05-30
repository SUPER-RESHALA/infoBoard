package com.simurg.infoboard.item;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TextItem extends MediaItem{
    public static final String TAG="TextItem";
    protected int duration;
    protected String text="Default text";
  public TextItem(File item, int duration) {
        super(item);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }
    public TextItem(String name, int duration) {
        super(name);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }
    public TextItem(String name, int duration, Date scheduledTime, Boolean isScheduled) {
        super(name,scheduledTime,isScheduled);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }
    public TextItem(File file, int duration, Date scheduledTime, Boolean isScheduled) {
        super(file,scheduledTime,isScheduled);
        this.duration = duration > 0 ? duration : MediaPlayerManager.DefaultDuration;
    }

    public int getDuration() {
        return duration;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getType() {
        return TYPE_TEXT;
    }

    @Override
    public void play(MediaPlayerManager mp) {
      mp.setPlaying(true);
   TextView textView= mp.getTextView();
        FileLogger.log(TAG, "TextView получен"+textView+"   "+ textView.toString());
    textView.setText(text);
    FileLogger.log(TAG,"Text of screen: "+ text);
    mp.hideVideoView();
    mp.hideImageView();
    mp.showTextView();
    FileLogger.log(TAG,"Stop Executor Called");
    mp.stopExecutor();
    mp.restartExecutor();
        FileLogger.log(TAG," Call PostDelayed");
        mp.getTimerThread().schedule(() -> {
            new Handler(Looper.getMainLooper()).post(mp::playNext); // Переключаемся на UI
        }, duration, TimeUnit.SECONDS);
FileLogger.log(TAG,"Запустился HANDLER Duration: "+ duration);
    }

    @Override
    public void playOnce(MediaPlayerManager mp) {
      FileLogger.log("play once text", "call");
        mp.setPlaying(false);
      mp.setDefaultView();
        TextView textView= mp.getTextView();
        FileLogger.log(TAG, "TextView получен"+textView+"   "+ textView.toString());
        textView.setText(text);
        FileLogger.log(TAG,"Text of screen: "+ text);
        mp.hideVideoView();
        mp.hideImageView();
        mp.showTextView();
        mp.stopHandler();
        mp.getHandler().postDelayed(mp::play,duration*1000);
    }

    @Override
    public String toString() {
        return "TextItem{" +
                "duration=" + duration +
                ", text='" + text + '\'' +
                ", name='" + name + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", isScheduled=" + isScheduled +
                ", file=" + file +
                '}';
    }
}
