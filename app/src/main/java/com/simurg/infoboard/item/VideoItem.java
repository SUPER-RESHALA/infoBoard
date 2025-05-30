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
    public VideoItem(File file, Date scheduledTime, Boolean isScheduled) {
        super(file,scheduledTime,isScheduled);
    }
    @Override
    public int getType() {
        return TYPE_VIDEO;
    }
    @Override
    public void play(MediaPlayerManager mediaPlayerManager) {
       mediaPlayerManager.setPlaying(true);
        if (!file.exists()){
            FileLogger.logError(TAG, "Файл не существует(File not exist) skip "+ file.getAbsolutePath());
            mediaPlayerManager.playNext();
            return;
        }

        long fileSize = file.length();
        if (fileSize > 300 * 1024 * 1024) {
            FileLogger.logError(TAG, "Видео слишком большое для play(Video so huge to play): " + file.getAbsolutePath());
            mediaPlayerManager.playNext();
            FileLogger.log("Video play, Attention", "skip bad file(playNext)" );
            return;
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
        FileLogger.log(TAG, "VideoItem play next");
            videoView.setOnCompletionListener(mp -> mediaPlayerManager.playNext());

    }
    @Override
    public void playOnce(MediaPlayerManager mediaPlayerManager) {
       mediaPlayerManager.setPlaying(false);
       mediaPlayerManager.setDefaultView();
        if (!file.exists()){
            FileLogger.logError(TAG, "Файл не существует(File not exist) skip "+ file.getAbsolutePath());
            mediaPlayerManager.playNext();
            return;
        }
        long fileSize = file.length();
        if (fileSize > 300 * 1024 * 1024) {
            FileLogger.logError(TAG, "Видео слишком большое для play(file so huge to play) skip " + file.getAbsolutePath());
            mediaPlayerManager.playNext();
            return;
        }
        VideoView videoView= mediaPlayerManager.getVideoView();
        FileLogger.log(TAG, "Получен videoView(Get VidView) "+ videoView.toString() + " | "+ videoView);
        if (!mediaPlayerManager.isVideoAtCenter()) {
        mediaPlayerManager.centerVideoView();}
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
 //       FileLogger.log(TAG, "VideoItem playOnce play next");
       videoView.setOnCompletionListener(mp -> mediaPlayerManager.play());
    }


    @Override
    public String toString() {
        return "VideoItem{" +
                "name='" + name + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", isScheduled=" + isScheduled +
                ", file=" + file +
                '}';
    }
}
