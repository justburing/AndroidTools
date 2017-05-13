package com.burning.smile.androidtools.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Burning on 2016/2/19.
 */
public class AndroidCommon {
    /** dip px互转 */
    public static int convertDpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /** 检查网络是否开启 */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    /** 检查gps是否开启 */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /** 获取一个apk文件的信息 */
    public static PackageInfo getApkInfo(String apkFile, Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES);
    }

    /** dip px互转 */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /** 显示软键盘, view为焦点view */
    public static void showKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /** 隐藏软键盘 */
    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** 跳转GPS设置界面 */
    public static void enableLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }

    /**
     * 点击空白处,关闭输入法软键盘
     * 在onTouchEvent方法中调用
     */
    public static void onHideSoftInput(AppCompatActivity context, MotionEvent event) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (context.getCurrentFocus() != null && context.getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 设置好adapter之后，调用此方法，动态调整listview高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 获取手机品牌
     * @return
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /**\
     * 获取手机型号
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取API等级
     * @return
     */
    public static int getBuildLevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取android版本
     * @return
     */
//    public static String getBuildVersion() {
//        return Build.VERSION.RELEASE;
//    }
//
//    public static int CALL_PHONE_CODE = 101;
//    public static int SEND_SMS_CODE = 102;
//    public static void callPhoneNumber(FragmentActivity context, String number) {
//        //用intent启动拨打电话
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
//        if(Build.VERSION.SDK_INT >= 23){
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE},
//                        CALL_PHONE_CODE);
//                return;
//            }
//        }
//        context.startActivity(intent);
//    }
//    /**
//     * 调起系统发短信功能
//     * @param phoneNumber
//     * @param message
//     */
//    public static void doSendSMSTo(FragmentActivity context,String phoneNumber,String message){
//        if(Build.VERSION.SDK_INT>=23){
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.SEND_SMS},
//                        SEND_SMS_CODE);
//                return;
//            }
//        }
//        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
//            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
//            intent.putExtra("sms_body", message);
//            context.startActivity(intent);
//        }
//    }
    /**
     * 匹配是否为手机号码
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
//        System.out.println(m.matches()+"---");
        return m.matches();
    }
    /** 检查意图是否有相应的app可以处理 */
    public static boolean hasAppHandle(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }
    /** 安装一个apk */
    public static void installApk(Context context, File file) {
        if (file != null && file.exists()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (hasAppHandle(context, i)) context.startActivity(i);
        }
    }


    /**
     * 获取android版本
     * @return
     */
    public static String getBuildVersion() {
        return Build.VERSION.RELEASE;
    }

}
