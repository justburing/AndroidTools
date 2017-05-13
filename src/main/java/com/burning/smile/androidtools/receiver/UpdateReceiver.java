package com.burning.smile.androidtools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.burning.smile.androidtools.tools.AndroidCommon;

import java.io.File;


/** Created by Burning on 2015/8/6. */
public class UpdateReceiver extends BroadcastReceiver {

	public static final String APK_PATH = "apk_path";

	@Override public void onReceive(Context context, Intent intent) {
		switch (intent.getAction()) {
			case "com.mycompany.yzmobile.INSTALL_NEW_VERSION":
				// 安装apk
				Intent i = new Intent(Intent.ACTION_VIEW);
				if (new File(intent.getStringExtra(APK_PATH)).exists()) {
					i.setDataAndType(Uri.parse("file://" + intent.getStringExtra(APK_PATH)), "application/vnd.android.package-archive");
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					if (AndroidCommon.hasAppHandle(context, i)) context.startActivity(i);
				}
				break;
		}
	}
}
