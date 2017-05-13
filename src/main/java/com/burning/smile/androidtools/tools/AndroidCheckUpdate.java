package com.burning.smile.androidtools.tools;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.burning.smile.androidtools.fragment.HintFragment;
import com.burning.smile.androidtools.services.AppUpdateService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Burning on 2016/5/16.
 */
public class AndroidCheckUpdate {
    public static boolean isUpdate = false;
    /**
     * 检查新版本
     */
    public static void checkVersion(final AppCompatActivity mContext, final CheckVersionInterface checkVersionInterface,String url){
//        String url = "http://api.fir.im/apps/latest/5680902d00fc74309400001e?api_token=082701147ad92d225ce9cc6f45ff3638";
        VolleyManager.getInstance(mContext).getRequestQ().add(new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                AndroidFragUtil.dismissDialog(mContext.getSupportFragmentManager());
                if (mContext.isFinishing()) return;
                try {
                    PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                    if (response.getInt("build") > packageInfo.versionCode) {
                        AndroidFragUtil.showDialog(mContext.getSupportFragmentManager(), HintFragment.getInstance("发现新版本", response.getString("changelog"), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                try {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("update_url")));
//                                    if (intent.resolveActivity(mContext.getPackageManager()) != null)
//                                        mContext.startActivity(intent);
//                                } catch (JSONException e) {
//                                    LogUtil.e("CheckVersion",e.getMessage());
//                                }
                                Intent intent = new Intent(mContext, AppUpdateService.class);
                                try {
                                    intent.putExtra(AppUpdateService.APP_URL, response.getString("direct_install_url"));
                                    intent.putExtra("version", response.getString("versionShort"));
                                    mContext.startService(intent);
                                    isUpdate = true;
                                    Toast.makeText(mContext,"开始下载",Toast.LENGTH_SHORT);
                                } catch (JSONException e) {
                                    LogUtil.e("CheckVersion",e.getMessage());
                                    Toast.makeText(mContext,"更新失败",Toast.LENGTH_SHORT);
                                }

                            }
                        }));
                        checkVersionInterface.findNewVersion(response);
                    } else {
                        checkVersionInterface.noNewVerson();
                    }
                } catch (Exception ignored) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckVersion","CheckVersion fail ! Because request http error !");
                AndroidFragUtil.dismissDialog(mContext.getSupportFragmentManager());
            }
        }));
    }
    public interface CheckVersionInterface{
        void findNewVersion(JSONObject packageInfo);
        void noNewVerson();
    }
}
