package com.freer.infusion.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.freer.infusion.R;
import com.freer.infusion.config.AppConfig;
import com.freer.infusion.entity.DataEntity;
import com.freer.infusion.entity.SocketEntity;
import com.freer.infusion.util.JsonUtils;
import com.freer.infusion.util.MediaHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhang on 2016/5/14.
 */
public class SocketDataProcess {

    public SocketDataProcess() {}

    public static void notificate(List<SocketEntity> List) {
        //根据不同工作状态，给用户提醒
        AppConfig appConfig = AppConfig.getInstance();
        for (SocketEntity socketEntity : List) {
            int workingState = socketEntity.WorkingState;
            notificate(workingState, appConfig);
        }
    }

    public static void notificate(int workingState, AppConfig appConfig) {
        if (workingState == WORK_FAST) {
            if (appConfig.isSharkFast()) {
                MediaHelper.startVibrate();
            }
            if (appConfig.isSoundFast()) {
                MediaHelper.startRingtone();
            }
        } else if (workingState == WORK_SLOW) {
            if (appConfig.isSharkSlow()) {
                MediaHelper.startVibrate();
            }
            if (appConfig.isSoundSlow()) {
                MediaHelper.startRingtone();
            }
        } else if (workingState == WORK_PAUSE) {
            if (appConfig.isSharkStop()) {
                MediaHelper.startVibrate();
            }
            if (appConfig.isSoundStop()) {
                MediaHelper.startRingtone();
            }
        } else if (workingState == WORK_POWER) {
            MediaHelper.startVibrate();
            MediaHelper.startRingtone();
        } else if (workingState == WORK_STOP) {
            MediaHelper.startVibrate();
            MediaHelper.startRingtone();
        }
    }

    public static final int WORK_NO = 0; //不用，白色
    public static final int WORK_BEGIN = 1; //进入工作状态，绿色
    public static final int WORK_NORMAL = 2; //正常工作，绿色
    public static final int WORK_FAST = 3; //过快，黄色
    public static final int WORK_SLOW = 4; //过慢，蓝色
    public static final int WORK_STOP = 5; //停止，红色色 包含设备故障
    public static final int WORK_POWER = 6; //低电，红色
    public static final int WORK_PAUSE = 7; //输液暂停，紫色
    public static final int WORK_ERROR = 10; //异常关机，红色

    /**
     * 根据不同工作状态，设置显示颜色
     * @param workState
     * @return
     */
    public static int getColor(Context context, int workState) {
        switch (workState) {
            case SocketDataProcess.WORK_NO:
                return ContextCompat.getColor(context, R.color.white);
            case SocketDataProcess.WORK_BEGIN:
                return ContextCompat.getColor(context, R.color.green);
            case SocketDataProcess.WORK_NORMAL:
                return ContextCompat.getColor(context, R.color.green);
            case SocketDataProcess.WORK_FAST:
                return ContextCompat.getColor(context, R.color.yellow);
            case SocketDataProcess.WORK_SLOW:
                return ContextCompat.getColor(context, R.color.blue);
            case SocketDataProcess.WORK_STOP:
                return ContextCompat.getColor(context, R.color.red);
            case SocketDataProcess.WORK_POWER:
                return ContextCompat.getColor(context, R.color.red);
            case SocketDataProcess.WORK_PAUSE:
                return ContextCompat.getColor(context, R.color.purple);
            case SocketDataProcess.WORK_ERROR:
                return ContextCompat.getColor(context, R.color.red);
            default:
                return ContextCompat.getColor(context, R.color.green);
        }
    }

    /**
     * 根据不同工作状态，显示进度条的背景颜色
     * @param context
     * @param workState
     * @return
     */
    public static Drawable getProgressDrawable(Context context, int workState) {
        switch (workState) {
            case SocketDataProcess.WORK_NO:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_green);
            case SocketDataProcess.WORK_BEGIN:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_green);
            case SocketDataProcess.WORK_NORMAL:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_green);
            case SocketDataProcess.WORK_FAST:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_yellow);
            case SocketDataProcess.WORK_SLOW:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_blue);
            case SocketDataProcess.WORK_STOP:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_purple);
            case SocketDataProcess.WORK_POWER:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_red);
            case SocketDataProcess.WORK_PAUSE:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_purple);
            case SocketDataProcess.WORK_ERROR:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_red);
            default:
                return ContextCompat.getDrawable(context, R.drawable.progressbar_green);
        }
    }

    public static Drawable getWarnDrawable(Context context, int workState) {
        switch (workState) {
            case SocketDataProcess.WORK_NO:
                return null;
            case SocketDataProcess.WORK_BEGIN:
                return null;
            case SocketDataProcess.WORK_NORMAL:
                return null;
            case SocketDataProcess.WORK_FAST:
                return ContextCompat.getDrawable(context, R.drawable.ic_up_warn);
            case SocketDataProcess.WORK_SLOW:
                return ContextCompat.getDrawable(context, R.drawable.ic_low_warn);
            case SocketDataProcess.WORK_STOP:
                return ContextCompat.getDrawable(context, R.drawable.ic_fix_warn);
            case SocketDataProcess.WORK_POWER:
                return ContextCompat.getDrawable(context, R.drawable.ic_power_warn);
            case SocketDataProcess.WORK_PAUSE:
                return ContextCompat.getDrawable(context, R.drawable.ic_pause_warn);
            case SocketDataProcess.WORK_ERROR:
                return ContextCompat.getDrawable(context, R.drawable.ic_fix_warn);
            default:
                return null;
        }
    }
}
