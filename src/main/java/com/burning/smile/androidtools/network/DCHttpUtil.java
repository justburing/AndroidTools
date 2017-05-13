package com.burning.smile.androidtools.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.burning.smile.androidtools.bean.UploadFile;
import com.burning.smile.androidtools.tools.LogUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 作者    Burning
 * 时间    2015/10/23 9:16
 * 描述
 */
public class DCHttpUtil {
    /** 下载图片 */
    public static Bitmap loadImage(@NonNull String url) {
        Bitmap bmp = null;
        try {
            URL imgUrl = new URL(url);
            bmp = BitmapFactory.decodeStream(imgUrl.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 上传一个字节流
     * @param path url 路径
     * @param file 文件字节流
     * @return
     */
    public static String uploadFile(String path, byte[] file) {
        String end = "\r\n";
        String Hyphens = "--";
        String boundary = java.util.UUID.randomUUID().toString();
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
	  /* 设定传送的method=POST */
            con.setRequestMethod("POST");
	  /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
      /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(Hyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"name\";filename=\"" + "filename" + "\"" + end);
            ds.writeBytes(end);
      /* 取得文件的FileInputStream */
            if(file!=null){
                ds.write(file);
            }
            ds.writeBytes(end);
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            ds.flush();
      /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            return b.toString();
        } catch (Exception e) {
            Log.e("myLog", e.getMessage());
            return "";
        }
    }

    /**
     * 上传多个文件字节流
     * @param path url 路径
     * @param fileList  字节流列表
     * @return
     */
    public static String uploadFiles(String path,List<UploadFile> fileList){
        String end = "\r\n";
        String Hyphens = "--";
         String boundary = java.util.UUID.randomUUID().toString();
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
      /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置超时时间 4秒*/
            con.setConnectTimeout(4000);
	  /* 设定传送的method=POST */
            con.setRequestMethod("POST");
	  /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             // 循环拼接文件字节流
             /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            if(fileList!=null&&fileList.size()>0){
                for(int i=0;i<fileList.size();i++){
                    ds.writeBytes(Hyphens + boundary + end);
                    ds.writeBytes("Content-Disposition: form-data; " + "name=\""+fileList.get(i).getmType()+"\";filename=\"" + fileList.get(i).getmName() + "\"" + end);
                    ds.writeBytes(end);
                 /* 取得文件的FileInputStream */
                    ds.write(fileList.get(i).getmFile());
                    ds.writeBytes(end);
                }
            }else{
                ds.writeBytes(Hyphens + boundary + end);
                ds.writeBytes(end);
            }
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
             /* 设置响应*/
            if(con.getResponseCode()!=200){
                return "{'success':false}";
            }
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            Log.e("uploadResponse",b.toString());
            return b.toString();
        } catch (Exception e) {
            Log.e("myLog", e.getMessage());
            return "{'success':false}";
        }
    }

    /**
     * 上传多个文件字节流
     * @param path url 路径
     * @param fileList  字节流列表
     * @return
     */
    public static String uploadFilesFormResponseByUTF8(String path,List<UploadFile> fileList){
        String end = "\r\n";
        String Hyphens = "--";
        String boundary = java.util.UUID.randomUUID().toString();
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
      /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置超时时间 4秒*/
            con.setConnectTimeout(4000);
	  /* 设定传送的method=POST */
            con.setRequestMethod("POST");
	  /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 循环拼接文件字节流
             /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            if(fileList!=null&&fileList.size()>0){
                for(int i=0;i<fileList.size();i++){
                    ds.writeBytes(Hyphens + boundary + end);
                    ds.writeBytes("Content-Disposition: form-data; " + "name=\""+fileList.get(i).getmType()+"\";filename=\"" + fileList.get(i).getmName() + "\"" + end);
                    ds.writeBytes(end);
                 /* 取得文件的FileInputStream */
                    ds.write(fileList.get(i).getmFile());
                    ds.writeBytes(end);
                }
            }else{
                ds.writeBytes(Hyphens + boundary + end);
                ds.writeBytes(end);
            }
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
             /* 设置响应*/
            if(con.getResponseCode()!=200){
                return "{'success':false}";
            }
            String ch;
            StringBuilder b = new StringBuilder();
            while ((ch = br.readLine()) != null) {
                b.append(ch);
            }
            ds.close();
         Log.e("uploadResponse",b.toString());
            return b.toString();
        } catch (Exception e) {
            Log.e("myLog", e.getMessage());
            return "{'success':false}";
        }
    }

    /**
     * 该请求同时带json参数以及图片录音等
     * @param path    请求的url
     * @param fileList  文件列表
     * @param jsonParams  json格式参数
     * @return
     */
    public static String uploadFilesWithJson(String path,List<UploadFile> fileList,String jsonParams){
        String end = "\r\n";
        String Hyphens = "--";
        String boundary = java.util.UUID.randomUUID().toString();
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
      /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置超时时间 4秒*/
            con.setConnectTimeout(4000);
	  /* 设定传送的method=POST */
            con.setRequestMethod("POST");
	  /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 循环拼接文件字节流
             /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());

            //首先拼接json参数
            if(jsonParams!=null){
                ds.writeBytes(Hyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\""+"jsonParam"+"\""+ end);
                ds.writeBytes("Content-Type:application/json"+end);
                ds.writeBytes(end);
                ds.writeBytes(jsonParams);
                ds.writeBytes(end);
            }else{
                return "{'success':'false','message':'json参数是空的'}";
            }
            if(fileList!=null&&fileList.size()>0){
                for(int i=0;i<fileList.size();i++){
                    ds.writeBytes(Hyphens + boundary + end);
                    ds.writeBytes("Content-Disposition: form-data; " + "name=\""+fileList.get(i).getmType()+"\";filename=\"" + fileList.get(i).getmName() + "\"" + end);
                    ds.writeBytes(end);
                 /* 取得文件的FileInputStream */
                    ds.write(fileList.get(i).getmFile());
                    ds.writeBytes(end);
                }
            }else{
                ds.writeBytes(Hyphens + boundary + end);
                ds.writeBytes(end);
            }
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
             /* 设置响应*/
            if(con.getResponseCode()!=200){
                return "{'success':false}";
            }
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            return b.toString();
        } catch (Exception e) {
            Log.e("myLog", e.getMessage());
            return "{'success':false}";
        }
    }
    /**
     * 把一个文件转化为字节
     * @param file
     * @return   byte[]
     * @throws Exception
     */
    public static byte[] getByte(File file) throws Exception
    {
        byte[] bytes = null;
        if(file!=null)
        {
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            if(length>Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
            {
                Log.e("DCHttpUtil","this file is max ");
                return null;
            }
            bytes = new byte[length];
            int offset = 0;
            int numRead = 0;
            while(offset<bytes.length&&(numRead=is.read(bytes,offset,bytes.length-offset))>=0)
            {
                offset+=numRead;
            }
            //如果得到的字节长度和file实际的长度不一致就可能出错了
            if(offset<bytes.length) {
                Log.e("DCHttpUtil","file length is error");
                return null;
            }
            is.close();
        }
        return bytes;
    }
    //json参数以流的形式上传
    public static String postJsonObject(String url, JSONObject jsonObject){
        URL object;
        HttpURLConnection con = null;
        OutputStream os = null;
        try {
            object = new URL(url);
            con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setReadTimeout(10000 /*milliseconds*/);
            con.setConnectTimeout(15000 /* milliseconds */);
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");

            os = con.getOutputStream();
            os.write(jsonObject.toString().getBytes("UTF-8"));
            os.flush();

            InputStream is = con.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            os.close();
            return b.toString();
        }catch (IOException e){
            return null;
        }finally {
            try {
                if(con!=null){
                    con.disconnect();
                }
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                LogUtil.e("postJsonObject",e.getMessage());
            }
        }

    }
}
