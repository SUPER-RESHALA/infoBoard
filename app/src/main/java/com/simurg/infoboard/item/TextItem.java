package com.simurg.infoboard.item;

import android.view.View;
import android.widget.TextView;

import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;

public class TextItem extends MediaItem{
    public static final String TAG="TextItem";
    protected int duration;
    protected String text="Default text";
  public TextItem(File item, int duration) {
        super(item);
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
   TextView textView= mp.getTextView();
        FileLogger.log(TAG, "TextView получен"+textView+"   "+ textView.toString());
    textView.setText(text);
    FileLogger.log(TAG,"Текст экрана "+ text);
    mp.hideVideoView();
    mp.hideImageView();
    mp.showTextView();
    FileLogger.log(TAG,"Stop HAndler Called");
    mp.stopHandler();
  mp.getHandler().postDelayed(mp::playNext, duration * 1000);
FileLogger.log(TAG,"Запустился HANDLER Duration: "+ duration);
    }

}
