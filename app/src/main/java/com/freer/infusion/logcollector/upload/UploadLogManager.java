package com.freer.infusion.logcollector.upload;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.freer.infusion.logcollector.capture.LogFileStorage;
import com.freer.infusion.logcollector.utils.LogCollectorUtility;

import java.io.File;


public class UploadLogManager {
	
	private static final String TAG = UploadLogManager.class.getName();
	
	private static UploadLogManager sInstance;
	
	private Context mContext;
	
	private HandlerThread mHandlerThread;
	
    private static volatile MyHandler mHandler;
	
    private volatile Looper mLooper;
    
    private volatile boolean isRunning = false;
    
    private String url;
    
    private HttpParameters params;
	
	private UploadLogManager(Context c){
		mContext = c.getApplicationContext();
		mHandlerThread = new HandlerThread(TAG + ":HandlerThread");
		mHandlerThread.start();
		
		
	}

	public static synchronized UploadLogManager getInstance(Context c){
		if(sInstance == null){
			sInstance = new UploadLogManager(c);
		}
		return sInstance;
	}
	
	public void uploadLogFile(String url , HttpParameters params){
		this.url = url;
		this.params = params;
		
		mLooper = mHandlerThread.getLooper();
		mHandler = new MyHandler(mLooper);
		if(mHandlerThread == null){
			return;
		}
		if(isRunning){
			return;
		}
		mHandler.sendMessage(mHandler.obtainMessage());
		isRunning = true;
	}
	
	private final class MyHandler extends Handler{

		public MyHandler(Looper looper) {
			super(looper);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleMessage(Message msg) {
			File logFile = LogFileStorage.getInstance(mContext).getUploadLogFile();
			if(logFile == null){
				isRunning = false;
				return;
			}
			try {
//				String result = HttpManager.uploadFile(url, params, logFile);
				MailSender sender = new MailSender("ketijie@163.com","6285067");
				String path = logFile.getAbsolutePath();
				try{
					String title = "崩溃日志";
					String body = "APP:" + LogCollectorUtility.getVerName(mContext) + "\n"+ "APP版本信息:" + LogCollectorUtility.getVerCode(mContext) +"\n"
							+ "OS版本信息:" + Build.VERSION.RELEASE +"\n" + "厂商:" + Build.MANUFACTURER + "型号:" + Build.MODEL ;
					String receivers = "ketijie@163.com" + "," + "690782486@qq.com";
					boolean result = sender.sendMail(title,body,"ketijie@163.com",receivers,path);
					if(result){
						LogFileStorage.getInstance(mContext).deleteUploadLogFile();
					}
				}catch (Exception e){
					System.out.println(e.toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				isRunning = false;
			}
		}
	}
	
}
