package com.simurg.infoboard.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.simurg.infoboard.file.FileHandler;
import com.simurg.infoboard.log.FileLogger;

import java.io.File;

public class ImageLoader {
    public static void setImage(ImageView imageView, File file) {
        if (!file.exists()) {
            FileLogger.logError("ImageLoader", "File not exist "+file.getAbsolutePath());
            return;
        }
    FileLogger.log("FIle is", file.getAbsolutePath());
        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Вычисляем коэффициент уменьшения
        options.inSampleSize = calculateInSampleSize(options, screenWidth, screenHeight);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (bitmap==null){
            FileLogger.logError("setImage", "Bitmap is null file: "+ file.getAbsolutePath());
            FileHandler.deleteFile(file);
            return;
        }
        int targetHeight = screenHeight;
        float scale = (float) targetHeight / bitmap.getHeight();
        int targetWidth = (int)(bitmap.getWidth() * scale);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        imageView.setImageBitmap(scaledBitmap);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
