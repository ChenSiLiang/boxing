/*
 *  Copyright (C) 2017 Bilibili
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bilibili.boxing.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * A file helper to make thing easier.
 *
 * @author ChenSL
 */
public class BoxingFileHelper {
    private static final String DEFAULT_SUB_DIR = "/bili/boxing";

    public static boolean createFile(String path) throws ExecutionException, InterruptedException {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        final File file = new File(path);
        return file.exists() || file.mkdirs();

    }

    @Nullable
    public static String getCacheDir(@NonNull Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        File cacheDir = context.getCacheDir();
        if (cacheDir == null) {
            BoxingLog.d("cache dir do not exist.");
            return null;
        }
        String result = cacheDir.getAbsolutePath() + "/boxing";
        try {
            BoxingFileHelper.createFile(result);
        } catch (ExecutionException | InterruptedException e) {
            BoxingLog.d("cache dir " + result + " not exist");
            return null;
        }
        BoxingLog.d("cache dir is: " + result);
        return result;
    }

    @Nullable
    public static String getBoxingPathInDCIM() {
        return getExternalDCIM();
    }

    /**
     * @return ..../DCIM/bili/boxing or null.
     */
    @Nullable
    public static String getExternalDCIM() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (file == null) {
                return null;
            }
            String result = file.getAbsolutePath() + DEFAULT_SUB_DIR;
            BoxingLog.d("external DCIM is: " + result);
            return result;
        }
        BoxingLog.d("external DCIM do not exist.");
        return null;
    }

    public static boolean isFileValid(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return isFileValid(file);
    }

    static boolean isFileValid(File file) {
        return file != null && file.exists() && file.isFile() && file.length() > 0 && file.canRead();
    }

    public static boolean isDirValid(String dirPath) {
        if (!TextUtils.isEmpty(dirPath)) {
            File dir = new File(dirPath);
            return dir.exists() && dir.isDirectory();
        }
        return false;
    }
}
