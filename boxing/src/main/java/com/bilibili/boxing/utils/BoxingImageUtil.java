package com.bilibili.boxing.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.text.TextUtils;

/**
 * Created by ChenSL on 2018/4/23.
 */
public class BoxingImageUtil {
    private static final int MAX_CAMERA_PHOTO_SIZE = 4 * 1024 * 1024;

    public static boolean rotateSourceFile(File file) throws IOException {
        return rotateSourceFile(file, MAX_CAMERA_PHOTO_SIZE);
    }

    public static boolean rotateSourceFile(File file, int size) throws IOException {
        if (file == null || !file.exists()) {
            return false;
        }
        FileOutputStream outputStream = null;
        Bitmap bitmap = null;
        Bitmap outBitmap = null;
        try {
            int degree = BoxingExifHelper.getRotateDegree(file.getAbsolutePath());
            if (degree == 0) {
                return true;
            }
            int quality = file.length() >= MAX_CAMERA_PHOTO_SIZE ? 90 : 100;
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            outBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            outputStream = new FileOutputStream(file);
            outBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            return true;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    BoxingLog.d("IOException when output stream closing!");
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (outBitmap != null) {
                outBitmap.recycle();
            }
        }
    }

    public static boolean isGif(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[3];
            int count = fis.read(buffer);
            return count == 3 && buffer[0] == 0x47 && buffer[1] == 0x49 && buffer[2] == 0x46;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int getSize(String path, int[] sizes) {
        return getSize(path, sizes, false);
    }

    public static int getSize(String path, int[] sizes, boolean calculateSize) {
        int size = 0;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            sizes[0] = options.outWidth;
            sizes[1] = options.outHeight;
            if (calculateSize) {
                size = options.outWidth * options.outHeight * getPixelSize(options);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return size;
    }

    private static int getPixelSize(BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // Bitmap.Config.ARGB_8888
            return 4;
        }
        return options.inPreferredConfig == Bitmap.Config.RGB_565 ? 2 : 4;
    }

}
