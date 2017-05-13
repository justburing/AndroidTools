package com.burning.smile.androidtools.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Burning on 2016/4/28.
 */
public class AndroidFileUtil {
    /** save object to file */
    public static boolean saveObject(Context context, Object obj, String name) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(name, Context.MODE_PRIVATE));
            oos.writeObject(obj);
            oos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** get object from file */
    public static <T> T getObject(Context context, String name) {
        try {
            ObjectInputStream oos = new ObjectInputStream(context.openFileInput(name));
            Object object = oos.readObject();
            oos.close();
            return (T) object;
        } catch (Exception e) {
            return null;
        }
    }

    /** remove object that in file */
    public static boolean removeObject(Context context, String name) {
        return context.deleteFile(name);
    }

    /** 以完整的文件名打开文件, 不存就新建文件. */
    public static File openFile(String filePath) {

        File file = new File(filePath);
        File path = file.getParentFile();
        if (path == null) {
            return null;
        }
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("===>>" + e.getMessage());
                return null;
            }
        }
        return file;
    }

    public static File openFile(File file) {
        return openFile(file.getAbsolutePath());
    }

    /** 以文件所在的路劲和文件名打开文件, 不存在就新建文件. */
    public static File openFile(String path, String fileName) {
        File file = new File(path, fileName);
        return openFile(file.getAbsolutePath());
    }

    /** 以文件所在的路劲和文件名打开文件, 不存在就新建文件. */
    public static File openFile(File path, String fileName) {
        if (path == null) return null;
        File file = new File(path.getAbsolutePath(), fileName);
        return openFile(file.getAbsolutePath());
    }

    /** 保存图片. */
    public static File savePhoto(Bitmap bmp, String path) {

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(openFile(path));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (stream != null) stream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return new File(path);
    }

    /** 保存图片. */
    public static File savePhoto(Bitmap bmp, File file) {

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (stream != null) stream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return file;
    }

    /** 从assets文件夹打开文件. */
    public static InputStream openFromAssets(String file, Context context) {

        try {
            return context.getAssets().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 检查SD卡是否可写. */
    public static boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean checkExternalStorage(Context context) {

        if (isExternalStorageWritable()) {
            return true;
        } else {
            Toast.makeText(context, "SD卡不可用!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /** 检查SD卡是否只读. */
    public static boolean isExternalStorageReadable() {

        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /** *//**
     * 文件转化为字节数组

     */
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {

        }
        return null;
    }
    /** *//**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null){
                try {
                    stream.close();
                } catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /** *//**
     * 从字节数组获取对象

     */
    public static Object getObjectFromBytes(byte[] objBytes) throws Exception{
        if (objBytes == null || objBytes.length == 0){
            return null;
        }
        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }

    /** *//**
     * 从对象获取一个字节数组
     */
    public static byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    }
}
