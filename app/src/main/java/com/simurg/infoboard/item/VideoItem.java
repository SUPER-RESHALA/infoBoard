package com.simurg.infoboard.item;

import android.media.MediaPlayer;
import android.widget.VideoView;

import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;
import java.util.Date;

public class VideoItem extends MediaItem {
    public static final String TAG="VideoItem";
   public VideoItem(File item) {
        super(item);
    }
    public VideoItem(String name) {
        super(name);
    }
    public VideoItem(String name, Date scheduledTime, Boolean isScheduled) {
        super(name,scheduledTime,isScheduled);
        }
    @Override
    public int getType() {
        return TYPE_VIDEO;
    }
    @Override
    public void play(MediaPlayerManager mediaPlayerManager) {
        if (!file.exists()){
            FileLogger.logError(TAG, "Файл не существует(File not exist) "+ file.getAbsolutePath());
        }
        VideoView videoView= mediaPlayerManager.getVideoView();
        FileLogger.log(TAG, "Получен videoView(Get VidView) "+ videoView.toString() + " | "+ videoView);


            mediaPlayerManager.hideImageView();
            mediaPlayerManager.hideTextView();
        videoView.setVideoPath(file.getAbsolutePath());
        FileLogger.log(TAG,"путь до видео настроен(setVideoPath)"+ file.getAbsolutePath());
            mediaPlayerManager.showVideoView();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    FileLogger.log(TAG,"Началось проигрывание видео(Start Playing Vid");
                    videoView.start();
                }
            });
        FileLogger.log(TAG, "Сработал play next");
            videoView.setOnCompletionListener(mp -> mediaPlayerManager.playNext());


    }
}
