package com.simurg.infoboard.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.simurg.infoboard.R;
import com.simurg.infoboard.file.FileType;
import com.simurg.infoboard.item.ImageItem;
import com.simurg.infoboard.item.MediaItem;
import com.simurg.infoboard.item.TextItem;
import com.simurg.infoboard.item.VideoItem;
import com.simurg.infoboard.log.FileLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerManager {
    public static final String TAG="MediaPlayerManager";
    protected TextView textView;
    protected ImageView imageView;
     protected VideoView videoView;
    public static int DefaultDuration=10;

    protected List<MediaItem> mediaFiles;
    protected List<MediaItem> sheduledPlaylist;
    protected int currentIndex = 0;
    /**
     * Sets the default duration for media playback.
     * @param defaultDuration The duration in seconds.
     */
    public static void setDefaultDuration(int defaultDuration) {
        DefaultDuration = defaultDuration;
    }
    protected Handler handler = new Handler();
    public Handler getHandler() {
        return handler;
    }
    /**
     * Constructor for MediaPlayerManager.
     * @param textView The TextView for displaying text content.
     * @param imageView The ImageView for displaying images.
     * @param videoView The VideoView for playing videos.
     */
    public MediaPlayerManager(TextView textView, ImageView imageView, VideoView videoView) {
        this.textView = textView;
        this.imageView = imageView;
        this.videoView = videoView;
    }
    public TextView getTextView() {
        return textView;
    }
    /**
     * Stretches the VideoView to match the screen size.
     */
public void stratchVideoView(){
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(params);
}
   public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public ImageView getImageView() {
        return imageView;
    }
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
    public VideoView getVideoView() {
        return videoView;
    }
    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }
    /**
     * Sets the playlist using a list of media items.
     * @param mediaFiles The list of media items.
     */
    public void setPlaylist(List<MediaItem> mediaFiles) {
        this.mediaFiles = mediaFiles;
        //currentIndex = 0;
        currentIndex = (mediaFiles == null || mediaFiles.isEmpty()) ? -1 : 0;
    }


    /**
     * Sets the playlist using an array of files.
     * @param files The array of media files.
     */
    public void setPlaylist(File[]files) {
        this.mediaFiles = createMediaList(files);
        //currentIndex = 0;
        currentIndex = (mediaFiles == null || mediaFiles.isEmpty()) ? -1 : 0;
    }




    /**
     * Plays the next media item in the playlist.
     */
    public synchronized void playNext() {
        if( mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1)FileLogger.logError("PlayNext","mediaFiles==null");
        if (mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1) return;

        currentIndex = (currentIndex + 1) % mediaFiles.size(); // Цикличный плейлист
        while (mediaFiles.get(currentIndex) == null) {
            FileLogger.log(TAG, "Ошибка: mediaFiles[" + currentIndex + "] == null, пропускаем");
            currentIndex = (currentIndex + 1) % mediaFiles.size(); // Пропускаем на следующий элемент
        }
        MediaItem nextItem = mediaFiles.get(currentIndex);
        if (nextItem != null) {
            nextItem.play(this);
        }
    }



//    public synchronized void playNext() {
//        if (mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1) return;
//
//        currentIndex = (currentIndex + 1) % mediaFiles.size(); // Цикличный плейлист
//        // mediaFiles.get(currentIndex).play(this);
//        MediaItem nextItem = mediaFiles.get(currentIndex);
//        if (nextItem != null) {
//            nextItem.play(this);
//        } else {
//            FileLogger.logError(TAG, "Ошибка: mediaFiles[" + currentIndex + "] == null");
//            playNext(); // Пропускаем и идем дальше
//        }
//    }

    /**
     * Starts playing the current media item.
     */
    public synchronized void play() {
        if( mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1)FileLogger.logError("Play","mediaFiles==null");
        if (mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1) return;
        FileLogger.log(TAG,"применено растягивание VideoView");
        stratchVideoView();
        if(currentIndex!=-1){
            mediaFiles.get(currentIndex).play(this);
        }else {
            FileLogger.logError("Play", "Currnet index= -1");
        }

      // mediaFiles.get(0).play(this);
    }
    /**
     * Stops playback and clears the playlist.
     */
    public synchronized void stop(){
        FileLogger.log(TAG,"Stop, mediaFiles.clear()");
setDefaultView();
stopHandler();
currentIndex=-1;
mediaFiles.clear();
    }
    public void resume(){}
//    public void playNext(){}
//    public boolean isPlaying(){}
    public void showTextView(){
        FileLogger.log(TAG," showTextView");
        textView.setVisibility(View.VISIBLE);
    }
    public void showVideoView(){
        FileLogger.log(TAG," showVideoView");
        videoView.setVisibility(View.VISIBLE);
    }
    public void showImageView(){
        FileLogger.log(TAG," showImageView");
        imageView.setVisibility(View.VISIBLE);
    }
    public void hideTextView() {
        FileLogger.log(TAG," hideTextView");
        textView.setVisibility(View.GONE);
    }
public void setDefaultView(){
        if (videoView.isPlaying()){
            FileLogger.log(TAG, "Остановили видео, setDefault");
            videoView.stopPlayback();
        }
    FileLogger.log(TAG,"hide Video, HideText, set Cat.jpg, stop handler");
        stopHandler();
        hideVideoView();
        hideTextView();
        showImageView();
    imageView.setImageResource(R.mipmap.cat);
}
    public void hideVideoView() {
        FileLogger.log(TAG," hideVideoView");
        videoView.setVisibility(View.GONE);
    }

    public void hideImageView() {
        FileLogger.log(TAG," hideImageView");
        imageView.setVisibility(View.GONE);
    }
    /**
     * Creates a media item list from an array of files.
     * @param files The array of media files.
     * @return A list of MediaItem objects.
     */
    public  List<MediaItem> createMediaList(File[] files) {
        List<MediaItem> mediaItems = new ArrayList<>();

        for (File file : files) {
            switch (FileType.DetectFileType(file)) {
                case MediaItem.TYPE_VIDEO:
                    mediaItems.add(new VideoItem(file));
                    break;
                case MediaItem.TYPE_IMAGE:
                    mediaItems.add(new ImageItem(file, DefaultDuration));
                    break;
                case MediaItem.TYPE_TEXT:
                    mediaItems.add(new TextItem(file, DefaultDuration));
                    break;
                default:
                    FileLogger.logError("MediaLoader", "Unknown file type: " + file.getAbsolutePath());
            }
        }

        return mediaItems;
    }


    protected void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE); // Полностью убирает элемент с экрана и из раскладки
        }
    }

public void stopHandler(){
        FileLogger.log(TAG,"Stop Handler Called");
    if (handler != null) {
        handler.removeCallbacksAndMessages(null);}
}
}
