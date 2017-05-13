package com.burning.smile.androidtools.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.burning.smile.androidtools.R;
import com.burning.smile.androidtools.receiver.UpdateReceiver;
import com.burning.smile.androidtools.tools.AndroidCommon;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.WYThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/** Created by Burning on 2015/8/6. */
public class AppUpdateService extends Service {

	public static final String APP_URL = "url";

	private NotificationManager mNotificationManager;
	private File                saveFile;
	private WeakHandler mHandler = new WeakHandler();

	@Override public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override public int onStartCommand(final Intent intent, int flags, int startId) {
		final String url = intent.getStringExtra(APP_URL);
		new WYThread() {
			@Override public void run() {
				try {
					URL appUrl = new URL(url);
					URLConnection conn = appUrl.openConnection();
					InputStream is = conn.getInputStream();

					if (!AndroidFileUtil.isExternalStorageWritable()) {
						mHandler.post(new Runnable() {
							@Override public void run() {
								Toast.makeText(AppUpdateService.this, "储存卡无法使用!", Toast.LENGTH_SHORT).show();
							}
						});
						return;
					}

					saveFile = new File(getExternalCacheDir(), "app.apk");
					if (saveFile.exists()) {
						// 当存在时判断版本号是否大于当前版本号, 否则无需重新下载
						PackageInfo info = getPackageManager().getPackageArchiveInfo(saveFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
						if (info != null && info.versionName.equals(intent.getStringExtra("version"))) {
							// 点击安装
							AndroidCommon.installApk(getApplicationContext(), saveFile);
							return;
						}
					}

					saveFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(saveFile);
					int contentLength = conn.getContentLength();
					int downloadedLengh = 0;

					byte[] bytes = new byte[4096];
					int buffer, index = 0;
					while ((buffer = is.read(bytes)) > 0) {
						fos.write(bytes, 0, buffer);

						// 通知栏进度条
						downloadedLengh += buffer;
						if (index++ % 100 == 0) { // 每400K更新进度条
							NotificationCompat.Builder builder = new NotificationCompat.Builder(AppUpdateService.this).setContentTitle("发现新版本!").setContentText(downloadedLengh * 100 / contentLength + "%").setLargeIcon(BitmapFactory.decodeResource
									(getResources(), R.drawable.ic_download_48dp)).setSmallIcon(R.drawable.ic_download_48dp).setProgress(contentLength, downloadedLengh, false).setOngoing(true);
							mNotificationManager.notify(1000, builder.build());
						}
					}

					// 点击安装
					PendingIntent install = PendingIntent.getBroadcast(AppUpdateService.this, 0, new Intent("com.mycompany.yzmobile.INSTALL_NEW_VERSION").putExtra(UpdateReceiver.APK_PATH, saveFile.getAbsolutePath()), 0);
					NotificationCompat.Builder builder = new NotificationCompat.Builder(AppUpdateService.this).setContentTitle("发现新版本!").setContentText("点击安装新版本").setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_48dp))
							.setSmallIcon(R.drawable.ic_download_48dp).setAutoCancel(true).setContentIntent(install);
					mNotificationManager.notify(1000, builder.build());

					fos.close();
					is.close();

					AndroidCommon.installApk(getApplicationContext(), saveFile);
				} catch (Exception e) {
					Log.e("mylog", e.toString());
					NotificationCompat.Builder builder = new NotificationCompat.Builder(AppUpdateService.this).setContentTitle("下载过程中发生错误").setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_48dp)).setSmallIcon(R.drawable
							.ic_download_48dp).setAutoCancel(true);
					mNotificationManager.notify(1000, builder.build());
				} finally {
//					BaseUtil.isUpdate = false;
					stopSelf();
				}
			}
		}.start();


		return super.onStartCommand(intent, flags, startId);
	}

	@Override public IBinder onBind(Intent intent) {
		return null;
	}

	@Override public void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
}
