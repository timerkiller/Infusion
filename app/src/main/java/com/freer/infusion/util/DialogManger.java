package com.freer.infusion.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.freer.infusion.R;
import com.freer.infusion.config.AppConfig;
import com.freer.infusion.entity.SocketEntity;
import com.google.gson.FieldAttributes;

/**
 * Created by 2172980000774 on 2016/5/11.
 */
public class DialogManger{

    private static final String mTag = "DialogManger";
    private static AppCompatEditText deviceNum;
    private static AppCompatEditText bedNum;
    private static AppCompatEditText lowerSpeed;
    private static AppCompatEditText upperSpeed;
    private static AppCompatEditText amount ;
    private static Button loudspeakerButton;
    private static Button urgentShutdownButton;
    /**
     * 主界面,床位信息设置弹窗
     */
    public interface OnMainPopupOkListener {
        public void onMessage(SocketEntity data,boolean isButtonClick);
    }

    public static PopupWindow getMainPopupWindow(Context context, final SocketEntity data,
                                                 final OnMainPopupOkListener onMainPopupOkListener) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewGroup contentView = (ViewGroup) layoutInflater.inflate(R.layout.popupwindow_main, null);
        View parentView = new View(context);
        final PopupWindow mainPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 实例化一个ColorDrawable做背景
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.white));
        mainPopupWindow.setBackgroundDrawable(dw);
        //必须设置不然不能点击
        mainPopupWindow.setFocusable(true);
        //设置点击外部可以关闭popupWindows
        mainPopupWindow.setOutsideTouchable(true);
        mainPopupWindow.setAnimationStyle(R.style.SlideBottom);
        mainPopupWindow.showAtLocation(parentView,
                Gravity.BOTTOM, 0, 0);

        // 设置窗口背景变暗
        final Window window = ((AppCompatActivity) context).getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        mainPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1.0f;
                window.setAttributes(lp);
            }
        });

        deviceNum = (AppCompatEditText) contentView.findViewById(R.id.device_number);
        bedNum = (AppCompatEditText) contentView.findViewById(R.id.bed_number);
        lowerSpeed = (AppCompatEditText) contentView.findViewById(R.id.lower_speed);
        lowerSpeed.setSelectAllOnFocus(true);
        upperSpeed = (AppCompatEditText) contentView.findViewById(R.id.upper_speed);
        upperSpeed.setSelectAllOnFocus(true);
        amount = (AppCompatEditText) contentView.findViewById(R.id.amount);
        amount.setSelectAllOnFocus(true);
//        final SwitchCompat voice = (SwitchCompat) contentView.findViewById(R.id.switch_voice);
//        final SwitchCompat power = (SwitchCompat) contentView.findViewById(R.id.switch_power);
        loudspeakerButton = (Button)contentView.findViewById(R.id.popupwindow_loudspeaker_button);
        urgentShutdownButton = (Button)contentView.findViewById(R.id.popupwindow_stop_button);

        // 设置设备号不可变更
        deviceNum.setFocusable(false);
        deviceNum.setBackgroundDrawable(null);
        // 根据软件版本设置床位号是否可变
        if (AppConfig.getInstance().getMode() == 0) {
            bedNum.setFocusable(false);
            bedNum.setBackgroundDrawable(null);
        }

        if (data != null) {
            String UxName = String.valueOf((Integer.valueOf(data.RFId)-1)*12 + Integer.valueOf(data.UxId)) ;
            Log.i(mTag,"From server data.RFId" + data.RFId + "UxId :" + data.UxId);
            deviceNum.setText(UxName);
            bedNum.setText(String.valueOf(data.BedId));
            lowerSpeed.setText(String.valueOf(data.LowLimitSpeed));
            upperSpeed.setText(String.valueOf(data.TopLimitSpeed));
            amount.setText(String.valueOf(data.ClientAction));
            // 设置声音状态
            boolean isOpenVoice;
            if (data.IsEnableMac == 101) isOpenVoice = true;
            else if (data.IsEnableMac == 100) isOpenVoice = false;
            else isOpenVoice = true;

            if(isOpenVoice) {
                loudspeakerButton.setText("喇叭单关");
                loudspeakerButton.setBackgroundColor(Color.GREEN);
            }else{
                loudspeakerButton.setText("喇叭单开");
                loudspeakerButton.setBackgroundColor(Color.GRAY);
            }

            urgentShutdownButton.setText("紧急停止");
            urgentShutdownButton.setBackgroundColor(Color.RED);
        }

        loudspeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button loudspeaker = (Button)v;
                if(loudspeaker.getText().equals("喇叭单开")){
                    saveData(data,101,0,onMainPopupOkListener,true);
                    loudspeaker.setText("喇叭单关");
                    loudspeakerButton.setBackgroundColor(Color.GREEN);
                }
                else {
                    saveData(data,100,0,onMainPopupOkListener,true);
                    loudspeaker.setText("喇叭单开");
                    loudspeaker.setBackgroundColor(Color.GRAY);
                }
            }
        });


        urgentShutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loudspeakerValue = 100;
                if(loudspeakerButton.getText().toString().equals("喇叭单关"))
                {
                    loudspeakerValue = 101;
                }
                else{
                    loudspeakerValue = 100;
                }

                saveData(data,loudspeakerValue,168,onMainPopupOkListener,true);
                //mainPopupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPopupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loudspeakerButton.getText().toString().equals("喇叭单关"))
                {
                    saveData(data,101,0,onMainPopupOkListener,false);
                }
                else{
                    saveData(data,100,0,onMainPopupOkListener,false);
                }
            }
        });

        return mainPopupWindow;
    }

    static void saveData(final SocketEntity data,int enableLoudspeaker,int urgentStop,final OnMainPopupOkListener onMainPopupOkListener,boolean isButtonClick){
        String strDeviceNum = deviceNum.getText().toString().equals("")?
                "0":deviceNum.getText().toString();
        String strBedNum = bedNum.getText().toString().equals("")?
                "0":bedNum.getText().toString();
        String strLowerSpeed = lowerSpeed.getText().toString().equals("")?
                "0":lowerSpeed.getText().toString();
        String strUpperSpeed = upperSpeed.getText().toString().equals("")?
                "0":upperSpeed.getText().toString();
        String strAmount = amount.getText().toString().equals("")?
                "0":amount.getText().toString();
        final int nDeviceNum = Integer.valueOf(strDeviceNum);
        final int nBedNum = Integer.valueOf(strBedNum);
        final int nLowerSpeed = Integer.valueOf(strLowerSpeed);
        final int nUpperSpeed = Integer.valueOf(strUpperSpeed);
        final int nAmount = Integer.valueOf(strAmount);

        data.BedId = nBedNum;
        data.LowLimitSpeed = nLowerSpeed;
        data.TopLimitSpeed = nUpperSpeed;
        data.ClientAction = nAmount;

        data.IsEnableMac = enableLoudspeaker;
        data.IsUrgentShutdown = urgentStop;
        Log.i(mTag,"save data" + "IsEnableMac:" + data.IsEnableMac + "IsUrgentShutDown:" + data.IsUrgentShutdown);
        onMainPopupOkListener.onMessage(data,isButtonClick);
    }

    /**
     * 确认退出APP对话框
     * @param context
     * @param onClickListener
     * @return
     */
    public static AlertDialog getQuitDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(context.getResources().getString(R.string.app_quit))
                .setNegativeButton(context.getResources().getString(R.string.app_cancel), null)
                .setPositiveButton(context.getResources().getString(R.string.app_ok), onClickListener)
                .create();
    }

    /**
     * 重试连接对话框
     * @param context
     * @param onRetryListener
     * @param onQuitListener
     * @return
     */
    public static AlertDialog getRetryDialog(Context context,
                                             DialogInterface.OnClickListener onRetryListener,
                                             DialogInterface.OnClickListener onQuitListener) {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(context.getResources().getString(R.string.app_retry_title))
                .setNegativeButton(context.getResources().getString(R.string.app_retry_action), onRetryListener)
                .setPositiveButton(context.getResources().getString(R.string.app_ok), onQuitListener)
                .create();
    }

    /**
     * 进度条对话框
     * @param context
     * @return
     */
    public static Dialog getProgressDialog(Context context) {

        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(R.layout.dialog_progress)
                .create();
    }
}