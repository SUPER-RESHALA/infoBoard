package com.simurg.infoboard.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.simurg.infoboard.file.FileHandler;
import com.simurg.infoboard.log.FileLogger;

import java.io.File;

public class ImageLoader {
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
    public static void setImage(ImageView imageView, File file) {
        if (!file.exists()) {
            FileLogger.logError("ImageLoader", "File not exist " + file.getAbsolutePath());
            return;
        }

        FileLogger.log("ImageLoader", "Loading file: " + file.getAbsolutePath());

        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // 1. Получаем размеры оригинала без загрузки
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        if (originalWidth <= 0 || originalHeight <= 0) {
            FileLogger.logError("ImageLoader", "Invalid image size");
            return;
        }

        // 2. Вычисляем масштаб до экрана, сохраняя пропорции
        float widthScale = (float) screenWidth / originalWidth;
        float heightScale = (float) screenHeight / originalHeight;
        float scale = Math.min(widthScale, heightScale); // Равномерное масштабирование

        int targetWidth = Math.round(originalWidth * scale);
        int targetHeight = Math.round(originalHeight * scale);

        // 3. Загружаем с учетом нужных размеров
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        Bitmap decodedBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        if (decodedBitmap == null) {
            FileLogger.logError("ImageLoader", "Bitmap decode failed: " + file.getAbsolutePath());
            FileHandler.deleteFile(file);
            return;
        }

        // 4. Масштабируем точно
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(decodedBitmap, targetWidth, targetHeight, true);
        imageView.setImageBitmap(scaledBitmap);
    }
}
