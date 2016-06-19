package com.freer.infusion.module.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.freer.infusion.R;
import com.freer.infusion.config.AppConfig;
import com.freer.infusion.entity.SocketEntity;
import com.freer.infusion.model.SocketDataProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/5/15.
 */
public class MainListAdapter extends BaseAdapter {

    private static final String mTag = "MainListAdapter";
    private Context mContext;
    private LayoutInflater mInflater;

    private List<SocketEntity> mDataList;

    public MainListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mDataList = new ArrayList<SocketEntity>();
    }

    public void setData(List<SocketEntity> dataList) {
        Log.i(mTag,">>>>>>setData");
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.item_main, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SocketEntity data = mDataList.get(position);
        holder.mView.setBackgroundColor(
                SocketDataProcess.getColor(mContext, data.WorkingState));
        if(AppConfig.getInstance().getMode() == 1){
            int baseDeviceNum = 0;
            if(data.UxId.equals("0C"))
            {
                baseDeviceNum = 12;
            }else if(data.UxId.equals("0A"))
            {
                baseDeviceNum = 10;
            }
            else if(data.UxId.equals("0B")){
                baseDeviceNum = 11;
            }
            else {
                baseDeviceNum = Integer.valueOf(data.UxId);
            }

            String UxName = String.valueOf((Integer.valueOf(data.RFId)-1)*12 + baseDeviceNum) ;
            if(data.BedId == 0 ){
                holder.mBedNum.setText(
                        String.format(mContext.getString(R.string.device), UxName));
            }
            else{
                holder.mBedNum.setText(
                        String.format("%s床[%s]", String.valueOf(data.BedId),UxName));
            }

        }else{
            holder.mBedNum.setText(
                    String.format(mContext.getString(R.string.bed_unit), String.valueOf(data.BedId)));
        }

        holder.mSpeed.setText(String.valueOf(data.CurrSpeed));
        holder.mSpeedLower.setText(String.valueOf(data.LowLimitSpeed));
        holder.mSpeedUpper.setText(String.valueOf(data.TopLimitSpeed));
        holder.mTotalNum.setText(
                String.format(mContext.getString(R.string.total_unit), String.valueOf(data.ClientAction)));
//        holder.mProgress.setProgressDrawable(
//                SocketDataProcess.getProgressDrawable(mContext, data.WorkingState));
        if (data.ClientAction != 0) {
            int process = data.RealProcess/data.ClientAction;
            holder.mProgress.setVisibility(View.VISIBLE);

            int value = (int)(((double)(data.ClientAction - 10)/data.ClientAction)*100);
            if (process >= value) {
                process = value;
                holder.mProgress.setProgressDrawable(
                        ContextCompat.getDrawable(mContext, R.drawable.progressbar_red));
            } else {
                holder.mProgress.setProgressDrawable(
                        ContextCompat.getDrawable(mContext, R.drawable.progressbar_green));
            }
            holder.mProgress.setProgress(process);
        } else {
            holder.mProgress.setVisibility(View.INVISIBLE);
        }
        holder.mImgvTip.setImageDrawable(
                SocketDataProcess.getWarnDrawable(mContext, data.WorkingState));
        return convertView;
    }

    public static class ViewHolder {

        View mView;
        TextView mBedNum;
        TextView mTotalNum;
        TextView mSpeed;
        TextView mSpeedLower;
        TextView mSpeedUpper;
        ProgressBar mProgress;
        ImageView mImgvTip;


        public ViewHolder(View itemView) {
            mView = itemView.findViewById(R.id.view);
            mBedNum = (TextView) itemView.findViewById(R.id.bed_number);
            mTotalNum = (TextView) itemView.findViewById(R.id.total_number);
            mSpeed = (TextView) itemView.findViewById(R.id.speed);
            mSpeedLower = (TextView) itemView.findViewById(R.id.speed_lower);
            mSpeedUpper = (TextView) itemView.findViewById(R.id.speed_upper);
            mProgress = (ProgressBar) itemView.findViewById(R.id.progress);
            mImgvTip = (ImageView) itemView.findViewById(R.id.imgv_tip);
        }

    }
}
