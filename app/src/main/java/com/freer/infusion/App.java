package com.freer.infusion;

import android.app.Application;
import android.util.Log;

import com.freer.infusion.logcollector.LogCollector;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application{
	
	private static App mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.mContext = this;

        Log.i("AppStart","start APP");
		LogCollector.setDebugMode(true);
		LogCollector.init(getApplicationContext(), "123", null);
		LogCollector.upload(false);
		//CrashReport.initCrashReport(getApplicationContext(), "900031994", true);
	}


	public static App getAppContext() {
		return mContext;
	}
}

