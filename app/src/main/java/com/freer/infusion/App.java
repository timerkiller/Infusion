package com.freer.infusion;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application{
	
	private static App mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.mContext = this;

		Log.i("AppStart","start APP");
		CrashReport.initCrashReport(getApplicationContext(), "900031994", true);
	}


	public static App getAppContext() {
		return mContext;
	}
}

