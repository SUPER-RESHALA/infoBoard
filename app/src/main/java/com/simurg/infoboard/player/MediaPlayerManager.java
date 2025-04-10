package com.simurg.infoboard.player;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
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
import com.simurg.infoboard.mydate.CustomDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MediaPlayerManager {
    public static final String TAG="MediaPlayerManager";
    protected TextView textView;
    protected ImageView imageView;
     protected VideoView videoView;
    public static int DefaultDuration=10;

    protected List<MediaItem> mediaFiles;
    protected List<MediaItem> scheduledPlaylist;
    protected int currentIndex = 0;
    protected int currentIndexScheduled=0;
    protected int delay=5000;
    protected Handler handler= new Handler();
  //  private int handlerCounter=0;
    //Activity mainAct;
protected boolean isPlaying=false;
    public Handler getHandler() {
        return handler;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    /**
     * Sets the default duration for media playback.
     * @param defaultDuration The duration in seconds.
     */
    public static void setDefaultDuration(int defaultDuration) {
        DefaultDuration = defaultDuration;
    }

    protected ScheduledExecutorService timerThread= Executors.newSingleThreadScheduledExecutor();
    // protected ScheduledExecutorService scheduleThread= Executors.newSingleThreadScheduledExecutor();
public void createPlaylists(ArrayList<MediaItem> items){
this.mediaFiles=items;
    this.scheduledPlaylist=new ArrayList<>();

    for (int i = 0; i < mediaFiles.size(); ) {
        MediaItem item = mediaFiles.get(i);
        if (item.isScheduled()) {
            scheduledPlaylist.add(item);
            mediaFiles.remove(i); // не увеличиваем i
        } else {
            i++;
        }
    }
    this.currentIndex = (this.mediaFiles == null || this.mediaFiles.isEmpty()) ? -1 : 0;
    this.currentIndexScheduled = (this.mediaFiles == null || this.mediaFiles.isEmpty()) ? -1 : 0;
   if (!sortScheduledPlaylist()) FileLogger.logError("createPlaylist", "scheduled playlist is null or error in sort");
}
public  boolean sortScheduledPlaylist(){
    if (this.scheduledPlaylist==null){return  false;}
    Collections.sort(this.scheduledPlaylist,comparator);
    return true;
}
    public boolean isScheduled(MediaItem mediaItem){
        if (!mediaItem.isScheduled()|| mediaItem.getScheduledTime().getTime()==0L){
            return false;
        }
        return true;
    }
         Comparator <MediaItem>comparator= new Comparator<>() {
            @Override
            public int compare(MediaItem o, MediaItem t1) {
                return o.getScheduledTime().compareTo(t1.getScheduledTime());
            }
        };

    public ScheduledExecutorService getTimerThread() {
        return timerThread;
    }
    public long getTimeDifference(MediaItem item){
        return  item.getScheduledTime().getTime()-CustomDate.getCurrentDate().getTime();
    }
public void startPlaylist(ArrayList<MediaItem> items){
      //  handlerCounter=0;
    stopHandler();
        if (mediaFiles==null ||scheduledPlaylist==null){
            setDefaultView();
        }else{
            stop();
            setDefaultView();
        }
        createPlaylists(items);
        play();
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
    public synchronized void play(){
        Log.i("play", "Вызван метод play()");
        if(mediaFiles!=null&&!mediaFiles.isEmpty()){
            playCycle();
        }
        if (!isPlaying){
            playCycle();
        }
        schedulerPlayer();
    }

    protected void schedulerPlayer(){
        if (scheduledPlaylist!=null&&!scheduledPlaylist.isEmpty()){
            Log.i("Schedule", "НЕ пуст не пуст");
            if (currentIndexScheduled!=-1 && currentIndexScheduled<scheduledPlaylist.size()){
                Log.i("Schedule", "Зашел в if!=-1");
                long time= getTimeDifference(scheduledPlaylist.get(currentIndexScheduled));
                //long time=scheduledPlaylist.get(currentIndexScheduled).getScheduledTime().getTime()-CustomDate.getCurrentDate().getTime();
                Log.i("time","time ="+ time+" File: "+ scheduledPlaylist.get(currentIndexScheduled).getFile().getName());
                if (time>=0){
                    if (currentIndexScheduled<scheduledPlaylist.size()){
                        Log.i("time>=","зашел");
                     //   handlerCounter++;
                        handler.postDelayed(()->{
                            Log.e(" ", "Выполняется процесс плана");
                            // Твоя задача
                            FileLogger.log("time>=0 play Scheduler","playing");
                            scheduledPlaylist.get(currentIndexScheduled).playOnce(this);
                            currentIndexScheduled++;
                            //scheduledPlaylist.remove(currentIndexScheduled).playOnce(this);
                        },time);


                    }

                }else {
                    if (isNormalDelay(time)>0){
                        if (currentIndexScheduled<scheduledPlaylist.size()){
                            Log.i("time<0","зашел");
                          //  handlerCounter++;
                            handler.postDelayed(()->{
                                FileLogger.log("NormDelay play Scheduler","playing");
                                scheduledPlaylist.get(currentIndexScheduled).playOnce(this);
                                currentIndexScheduled++;

                                // scheduledPlaylist.remove(currentIndexScheduled).playOnce(this);
                            },time);
                        }
                        //TODO запуск таймера
                    }else {
                        currentIndexScheduled++;
                        schedulerPlayer();
                    }

                }

            }


        }
    }
    public long isNormalDelay(long time){
        if (Math.abs(time)>delay){
            return -1;
        }
        return CustomDate.getCurrentDate().getTime()+3000;
    }

    /**
     * Starts playing the current media item.
     */
    public synchronized void playCycle() {
        if( mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1)FileLogger.logError("Play","mediaFiles==null");
        if (mediaFiles == null || mediaFiles.isEmpty() || currentIndex == -1) return;
        FileLogger.log(TAG,"применено растягивание VideoView");
        stratchVideoView();
        if(currentIndex!=-1){
            mediaFiles.get(currentIndex).play(this);
        }else {
            FileLogger.logError("Play", "Currnet index= -1");
        }
    }

    /**
     * Stops playback and clears the playlist.
     */
    public synchronized void stop(){
        FileLogger.log(TAG,"Stop, mediaFiles.clear()");
        setPlaying(false);
        setDefaultView();
        stopExecutor();
        currentIndex=-1;
        mediaFiles.clear();
        scheduledPlaylist.clear();
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
    setPlaying(false);
        if (videoView.isPlaying()){
            FileLogger.log(TAG, "Top video, setDefault");
            videoView.stopPlayback();
        }
    FileLogger.log(TAG,"hide Video, HideText, set Cat.jpg, stop executor");
        stopExecutor();

        hideVideoView();
        hideTextView();
        showImageView();
    imageView.setImageResource(R.mipmap.cat);
}
    public void hideVideoView() {
        FileLogger.log(TAG," hideVideoView");
        videoView.setVisibility(View.GONE);
    }
public void pause(){
    setPlaying(false);
    if (videoView.isPlaying()){
        FileLogger.log(TAG, "Top video, setDefault");
        videoView.stopPlayback();
    }
    FileLogger.log(TAG,"hide Video, HideText, set Cat.jpg, stop executor");
    stopExecutor();
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

public void stopExecutor(){
    FileLogger.log(TAG, "Stop Executor Called");
if (timerThread!=null&&!timerThread.isShutdown()){
    timerThread.shutdownNow();
    timerThread= Executors.newSingleThreadScheduledExecutor();
}
}
public void stopHandler(){
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
}

//    public void stopSchedulerThread(){
//        FileLogger.log(TAG, "Stop SchedulerThread Called");
//        if (scheduleThread!=null&&!scheduleThread.isShutdown()){
//            scheduleThread.shutdownNow();
//            scheduleThread= Executors.newSingleThreadScheduledExecutor();
//        }
//    }
}
