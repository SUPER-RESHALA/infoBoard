package com.simurg.infoboard.item;

import static com.simurg.infoboard.ui.ImageLoader.setImage;

import android.os.Handler;
import android.os.Looper;

import com.simurg.infoboard.log.FileLogger;
import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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


//    public void setImageGOOOOD(ImageView imageView){
//        if (file.exists()){
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//            DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
//            int screenWidth = metrics.widthPixels;
//            int screenHeight = metrics.heightPixels;
//
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);
//            imageView.setImageBitmap(scaledBitmap);
//        } else {
//            FileLogger.logError(TAG, "File not exist");
//        }
//    }

//    public void setImage(ImageView imageView) {
//        if (file.exists()) {
//            //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//            int screenWidth = metrics.widthPixels;
//            int screenHeight = metrics.heightPixels;
//
//            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), screenWidth, screenHeight);
//            imageView.setImageBitmap(bitmap);
//        } else {
//            FileLogger.logError(TAG, "File not exist");
//        }
//    }


//    public void setImage(ImageView imageView) {
//        if (file.exists()) {
//            imageView.post(() -> {
//                int targetWidth = imageView.getWidth();
//                int targetHeight = imageView.getHeight();
//
//                if (targetWidth == 0 || targetHeight == 0) {
//                    targetWidth = 800;
//                    targetHeight = 600;
//                }
//
//                Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), targetWidth, targetHeight);
//                imageView.setImageBitmap(bitmap);
//            });
//        } else {
//            FileLogger.logError(TAG, "File not exist");
//        }
//    }

//
//    public void setImage(ImageView imageView) {
//        if (file.exists()) {
//            // Получаем размеры ImageView
//            int targetWidth = imageView.getWidth();
//            int targetHeight = imageView.getHeight();
//
//            // Если размеры недоступны (View ещё не отрисован), ставим разумные значения по умолчанию
//            if (targetWidth == 0 || targetHeight == 0) {
//                targetWidth = 800;
//                targetHeight = 600;
//            }
//
//            // Загружаем уменьшенный Bitmap
//            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), targetWidth, targetHeight);
//            imageView.setImageBitmap(bitmap);
//        } else {
//            FileLogger.logError(TAG, "File not exist");
//        }
//    }




//
//    public void setImage( ImageView imageView){
//        if (file.exists()){
//            Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
//imageView.setImageBitmap(bitmap);
//        }else {
//            FileLogger.logError(TAG,"File not exist");
//        }
//    }
    @Override
    public void play(MediaPlayerManager mp) {
    FileLogger.log("Play ImageItem","SetImage call");
    mp.setPlaying(true);
    setImage(mp.getImageView(),super.file);
    mp.hideTextView();
    mp.hideVideoView();
    mp.showImageView();
    FileLogger.log(TAG,"Stop Executor Called");
    mp.stopExecutor();
    FileLogger.log(TAG," Call PostDelayed");
        mp.getTimerThread().schedule(() -> {
            new Handler(Looper.getMainLooper()).post(mp::playNext); // Переключаемся на UI
        }, duration, TimeUnit.SECONDS);
    }

    @Override
    public void playOnce(MediaPlayerManager mp) {
        mp.setPlaying(false);
        mp.setDefaultView();
        FileLogger.log("PlayOnce Image","SetImage call");
        setImage(mp.getImageView(),super.file);
        mp.hideTextView();
        mp.hideVideoView();
        mp.showImageView();
        FileLogger.log(TAG,"Stop Handler Called");
        mp.stopHandler();
        mp.getHandler().postDelayed(mp::play,duration*1000);
      //  FileLogger.log(TAG," Call PostDelayed");
    }




//    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inJustDecodeBounds = false;
//
//        return BitmapFactory.decodeFile(path, options);
//    }
//
//    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        int height = options.outHeight;
//        int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            int halfHeight = height / 2;
//            int halfWidth = width / 2;
//
//            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }





//    @Override
//    public void playOnce(MediaPlayerManager mp) {
//        mp.handler.post(() -> {
//            mp.hideTextView();
//            mp.hideVideoView();
//            mp.showImageView();
//
//            FileLogger.log("PlayOnce Image", "SetImage call");
//
//            if (file.exists()) {
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                if (bitmap != null) {
//                    mp.getImageView().setImageBitmap(bitmap);
//                    FileLogger.log("PlayOnce Image", "Bitmap успешно установлен");
//                } else {
//                    FileLogger.logError("PlayOnce Image", "Bitmap decode вернул null");
//                }
//            } else {
//                FileLogger.logError("PlayOnce Image", "Файл не существует: " + file.getAbsolutePath());
//            }
//
//            FileLogger.log(TAG, "Stop Executor Called");
//        });
//    }




    @Override
    public String toString() {
        return "ImageItem{" +
                "duration=" + duration +
                ", name='" + name + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", isScheduled=" + isScheduled +
                ", file=" + file +
                '}';
    }
}
