package com.simurg.infoboard.item;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.ViewGroup;
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
            Uri uri= Uri.fromFile(file);
            videoView.setVideoURI(uri);
        //videoView.setVideoPath(file.getAbsolutePath());
        FileLogger.log(TAG,"путь до видео настроен(setVideoPath)"+ file.getAbsolutePath());
            mediaPlayerManager.showVideoView();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                  tuneVideoScale(mediaPlayer,videoView);
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
        Uri uri= Uri.fromFile(file);
        videoView.setVideoURI(uri);
        FileLogger.log(TAG,"путь до видео настроен(setVideoPath)"+ file.getAbsolutePath());
        mediaPlayerManager.showVideoView();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
              tuneVideoScale(mediaPlayer,videoView);
                FileLogger.log(TAG,"Началось проигрывание видео(Start Playing Vid");
                videoView.start();
            }
        });
 //       FileLogger.log(TAG, "VideoItem playOnce play next");
       videoView.setOnCompletionListener(mp -> mediaPlayerManager.play());
    }
protected void tuneVideoScale(MediaPlayer mediaPlayer, VideoView videoView){
    //Get your video's width and height
    int videoWidth = mediaPlayer.getVideoWidth();
    int videoHeight = mediaPlayer.getVideoHeight();
    //Get VideoView's current width and height
    int videoViewWidth = videoView.getWidth();
    int videoViewHeight = videoView.getHeight();
    float xScale = (float) videoViewWidth / videoWidth;
    float yScale = (float) videoViewHeight / videoHeight;

    //For Center Crop use the Math.max to calculate the scale
    //float scale = Math.max(xScale, yScale);
    //For Center Inside use the Math.min scale.
    //I prefer Center Inside so I am using Math.min
    float scale = Math.min(xScale, yScale);

    float scaledWidth = scale * videoWidth;
    float scaledHeight = scale * videoHeight;

    //Set the new size for the VideoView based on the dimensions of the video
    ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
    layoutParams.width = (int)scaledWidth;
    layoutParams.height = (int)scaledHeight;
    videoView.setLayoutParams(layoutParams);
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
