package com.burning.smile.androidtools.tools;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

/**
 * Created by Burning on 2016/8/6.
 */

public class AndroidCameraUtil {
    public static final int CAMERA_TAKEPHOTO = 1001;
    public static final int CAMERA_CHOOSEPHOTO = 1002;

    public static final String dir = Environment.getExternalStorageDirectory() + "/androidDev/";
    private static AndroidCameraUtil camera;
    private AppCompatActivity context;
    private Bitmap imgBitMap;
    public static AndroidCameraUtil getInstance(AppCompatActivity context){
        if(camera==null){
            camera = new AndroidCameraUtil();
        }
        camera.context = context;
        return camera;
    }

    public  String getDir(){
        return     Environment.getExternalStorageDirectory() + "/androidDev/";
    }
    public  String createPicName(){
        return "androidDev" + System.currentTimeMillis() + ".jpg";
    }
    //从相册选完照片后，返回照片路径
    public  String getFilePathFromAlbum(Intent data){
        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor query = context.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        assert query != null;
        query.moveToFirst();
        int columnIndex = query.getColumnIndex(filePathColumns[0]);
        String picturePath = query.getString(columnIndex);
        query.close();
        return picturePath;
    }

    //相机拍照
    public  void takePhoto(File file){
        Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fDir = new File(dir);
        if (!fDir.exists()) {
            fDir.mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        in.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        context.startActivityForResult(in, CAMERA_TAKEPHOTO);
    }
    public  void choosePhoto(){
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(in, CAMERA_CHOOSEPHOTO);
    }

    /**
     * 创建图像列表
     * @param rl 根布局
     * @param picPath 图片路径
     * @param listener  图片监听事件
     */
    public Bitmap createAlbum(RelativeLayout rl, String picPath, View.OnClickListener listener){
        //图片压缩
        if (imgBitMap != null) {
            imgBitMap.recycle();//释放内存
        }
        final int c = rl.getChildCount();
        final int m = 14;
        final int w = (rl.getWidth() - m * 4) / 5;
        ImageView iv = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, w);
        lp.setMargins(c % 5 * (w + m), c / 5 * (c < 5 ? 0 : m + w), 0, 0);
        imgBitMap = AndroidBitmapUtil
                .decodeSampledBitmapFromFile(picPath, 800, 600);
        iv.setLayoutParams(lp);
        iv.setImageBitmap(AndroidBitmapUtil.cutBitmap(imgBitMap, w, w));
        iv.setId(c);
        iv.setTag(picPath);
        rl.addView(iv);
        iv.setOnClickListener(listener);
        return imgBitMap;
    }

    //CleanGreenActivity 专用
    public Bitmap createAlbumWithScreenWidth(Context context,RelativeLayout rl, String picPath, View.OnClickListener listener){
        //图片压缩
        if (imgBitMap != null) {
            imgBitMap.recycle();//释放内存
        }
        final int c = rl.getChildCount()-1;
        final int m = 14;
        final int w = (ScreenUtils.getScreenWidth(context) - m * 4) / 4;
        ImageView iv = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, w);
        lp.setMargins(c % 4 * (w + m), c / 4 * (c < 4 ? 0 : m + w), 0, 0);
        imgBitMap = AndroidBitmapUtil
                .decodeSampledBitmapFromFile(picPath, 800, 600);
        iv.setLayoutParams(lp);
        iv.setImageBitmap(AndroidBitmapUtil.cutBitmap(imgBitMap, w, w));
        iv.setId(c);
        iv.setTag(picPath);
        rl.addView(iv);
        iv.setOnClickListener(listener);
        return imgBitMap;
    }
    /**
     * 创建图像列表
     * @param rl 根布局
     * @param picPath 图片路径
     * @param width 图片大小
     * @param listener  图片监听事件
     */
    public Bitmap createAlbumBySize(RelativeLayout rl, String picPath,int width, View.OnClickListener listener){
        //图片压缩
        if (imgBitMap != null) {
            imgBitMap.recycle();//释放内存
        }
        final int c = rl.getChildCount();
        final int m = 14;
        final int w = width;
        ImageView iv = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dip2px(context, w),dip2px(context, w));
        lp.setMargins((c % 5) *(dip2px(context,w)+m), c / 5 * (c < 5 ? 0 : (w+m)), m, 0);
        imgBitMap = AndroidBitmapUtil
                .decodeSampledBitmapFromFile(picPath, 800, 600);
        iv.setLayoutParams(lp);
        iv.setImageBitmap(AndroidBitmapUtil.cutBitmap(imgBitMap, w, w));
        iv.setId(c);
        iv.setTag(picPath);
        rl.addView(iv);
        iv.setOnClickListener(listener);
        return imgBitMap;
    }
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case CAMERA_CHOOSEPHOTO:

            case CAMERA_TAKEPHOTO:
                break;
            default:
                break;
        }
    }

    public void clearDirectory(){
        File[] files = new File(getDir()).listFiles();
        if(files!=null){
            for(int i=0;i<files.length;i++){
                File file = files[i];
                file.delete();
            }
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



}
