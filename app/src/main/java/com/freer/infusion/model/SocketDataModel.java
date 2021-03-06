package com.freer.infusion.model;

import android.util.Log;

import com.freer.infusion.config.AppConfig;
import com.freer.infusion.entity.DataEntity;
import com.freer.infusion.entity.SocketEntity;
import com.freer.infusion.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 2172980000774 on 2016/5/17.
 */
public class SocketDataModel {

    private static final String mTag="SocketDataModel";
    private List<SocketEntity> mDataList;
    private Map<String, SocketEntity> mDataMap;

    public SocketDataModel() {
        mDataList = new ArrayList<>();
        mDataMap = new HashMap<>();
    }

    public void setData(List<SocketEntity> dataList) {
        this.setData(dataList, false);
    }

    public void setData(List<SocketEntity> dataList, boolean isFollow) {
        for (SocketEntity fromS : dataList) {
            this.setData(fromS, isFollow);
        }
    }

    /**
     * 更新数据
     * @param data
     */
    public void setData(SocketEntity data, boolean isFollow) {
        String bedId = String.valueOf(data.BedId);
        Log.i(mTag,"setData ->> bedId" + bedId);
        if (mDataMap.containsKey(data.UxName)) {
            Log.i(mTag,"mData contain " + data.UxName);
            if (data.WorkingState == SocketDataProcess.WORK_NO ||
                    data.WorkingState < 0 ||
                    data.WorkingState > 7) {
                mDataMap.remove(data.UxName);
                Log.e(mTag,"setData return from workingState:" + data.WorkingState);
                return;
            }
            if (followBed != null && followBed.size()!=0) {
                if (myBed != null && myBed.size()!=0) {
                    if (!followBed.containsKey(bedId)) {
                        if (myBed.containsKey(bedId)) {
                            if (isFollow &&
                                    (data.WorkingState == SocketDataProcess.WORK_BEGIN ||
                                        data.WorkingState == SocketDataProcess.WORK_NORMAL)) {
                                mDataMap.remove(data.UxName);
                                Log.w(mTag,"isFollow work return");
                                return;
                            }
                        } else {
                            mDataMap.remove(data.UxName);
                            return;
                        }
                    }
                } else {
                    if (!followBed.containsKey(bedId)) {
                        mDataMap.remove(data.UxName);
                        return;
                    }
                }
            } else {
                if (myBed != null && myBed.size()!=0) {
                    if (!myBed.containsKey(bedId)) {
                        mDataMap.remove(data.UxName);
                        return;
                    } else {
                        if (isFollow &&
                                (data.WorkingState == SocketDataProcess.WORK_BEGIN ||
                                    data.WorkingState == SocketDataProcess.WORK_NORMAL)) {
                            mDataMap.remove(data.UxName);
                            return;
                        }
                    }
                } else {
                    mDataMap.clear();
                    return;
                }
            }
            SocketEntity fromL = mDataMap.get(data.UxName);
            data.CurrSpeed = 0==data.CurrSpeed &&
                    (data.WorkingState!=SocketDataProcess.WORK_PAUSE &&
                            data.WorkingState!=SocketDataProcess.WORK_STOP) ?
                    fromL.CurrSpeed : data.CurrSpeed;
            data.CurrSpeed = -1==data.CurrSpeed?fromL.CurrSpeed:data.CurrSpeed;
            data.TopLimitSpeed = -1==data.TopLimitSpeed?fromL.TopLimitSpeed:data.TopLimitSpeed;
            data.LowLimitSpeed = -1==data.LowLimitSpeed?fromL.LowLimitSpeed:data.LowLimitSpeed;
            data.ClientAction = -1==data.ClientAction?fromL.ClientAction:data.ClientAction;
            data.RealProcess = 0==data.RealProcess?fromL.RealProcess:data.RealProcess;
            data.WarnProcess = 0==data.WarnProcess?fromL.WarnProcess:data.WarnProcess;
            mDataMap.put(data.UxName, data);
        } else {
            Log.i(mTag,"mData not contain UxName:" + data.UxName);

            if (data.WorkingState == SocketDataProcess.WORK_NO ||
                    data.WorkingState < 0 ||
                    data.WorkingState > 7) {
                Log.e(mTag,"Working state error" + data.WorkingState);
                return;
            }
            if (followBed != null && followBed.size()!=0) {
                if (myBed != null && myBed.size()!=0) {
                    if (!followBed.containsKey(bedId)) {
                        if (myBed.containsKey(bedId)) {
                            if (isFollow &&
                                    (data.WorkingState == SocketDataProcess.WORK_BEGIN ||
                                        data.WorkingState == SocketDataProcess.WORK_NORMAL)) {
                                return;
                            }
                        } else {
                            Log.i(mTag,"not contain bedID:" + bedId);
                            return;
                        }
                    }
                } else {
                    if (!followBed.containsKey(bedId)) {
                        return;
                    }
                }
            } else {
                if (myBed != null && myBed.size()!=0) {
                    if (!myBed.containsKey(bedId)) {
                        return;
                    } else {
                        if (isFollow &&
                                (data.WorkingState == SocketDataProcess.WORK_BEGIN ||
                                        data.WorkingState == SocketDataProcess.WORK_NORMAL)) {
                            return;
                        }
                    }
                } else {
                    mDataMap.clear();
                    return;
                }
            }
            data.CurrSpeed = -1==data.CurrSpeed?0:data.CurrSpeed;
            data.TopLimitSpeed = -1==data.TopLimitSpeed?0:data.TopLimitSpeed;
            data.LowLimitSpeed = -1==data.LowLimitSpeed?0:data.LowLimitSpeed;
            data.ClientAction = -1==data.ClientAction?0:data.ClientAction;
            mDataMap.put(data.UxName, data);
        }
    }

    /**
     * 更新数据，不添加新数据
     * @param data
     */
    public void setDataNoAdd(SocketEntity data) {
        //判断当前数据中如果已经含有这段数据，则更新
        if (mDataMap.containsKey(data.UxName)) {
            if (data.WorkingState == SocketDataProcess.WORK_NO) {
                mDataMap.remove(data.UxName);
                return;
            }
            SocketEntity fromL = mDataMap.get(data.UxName);
            data.CurrSpeed = 0==data.CurrSpeed &&
                    (data.WorkingState!=SocketDataProcess.WORK_PAUSE &&
                            data.WorkingState!=SocketDataProcess.WORK_STOP) ?
                    fromL.CurrSpeed : data.CurrSpeed;
            fromL.CurrSpeed = -1==data.CurrSpeed?fromL.CurrSpeed:data.CurrSpeed;
            fromL.TopLimitSpeed = -1==data.TopLimitSpeed?fromL.TopLimitSpeed:data.TopLimitSpeed;
            fromL.LowLimitSpeed = -1==data.LowLimitSpeed?fromL.LowLimitSpeed:data.LowLimitSpeed;
            fromL.ClientAction = -1==data.ClientAction?fromL.ClientAction:data.ClientAction;
            fromL.RealProcess = 0==data.RealProcess?fromL.RealProcess:data.RealProcess;
            fromL.WarnProcess = 0==data.WarnProcess?fromL.WarnProcess:data.WarnProcess;
            mDataMap.put(fromL.UxName, fromL);
        }
    }

    public List<SocketEntity> getData() {
        //先清空一次list
        mDataList.clear();
        for (Iterator it = mDataMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, SocketEntity> entry = (Map.Entry<String, SocketEntity>) it.next();
            String bedId = String.valueOf(entry.getValue().BedId);
            if (bedId != null) {
                if (myBed != null && myBed.size() > 0) {
                    if (followBed != null && followBed.size() > 0) {
                        if (!myBed.containsKey(bedId) && !followBed.containsKey(bedId)) {
                            it.remove();
                            continue;
                        }
                    } else {
                        if (!myBed.containsKey(bedId)) {
                            it.remove();
                            continue;
                        }
                    }
                } else {
                    if (followBed != null && followBed.size() > 0) {
                        if (!followBed.containsKey(bedId)) {
                            it.remove();
                            continue;
                        }
                    } else {
                        it.remove();
                        continue;
                    }
                }
                mDataList.add(entry.getValue());
            }
        }
//        for (Map.Entry<String, SocketEntity> entry : mDataMap.entrySet()) {
//            mDataList.add(entry.getValue());
//        }
        Collections.sort(mDataList, new Comparator<SocketEntity>() {
            @Override
            public int compare(SocketEntity lhs, SocketEntity rhs) {
                return lhs.BedId - rhs.BedId;
            }
        });
        return mDataList;
    }

    private HashMap<String, String> myBed;
    private HashMap<String, String> followBed;
    /**
     * 初步处理，筛选出我们选定的床位数据
     * @param message
     * @param isFollow
     */
    public void processData(String message, boolean isFollow) {

        DataEntity dataEntity = JsonUtils.fromJson(message, DataEntity.class);
        if (dataEntity == null || dataEntity.d == null) {
            Log.e(mTag,"phase json data error");
            return;
        }

        Log.i(mTag,"get json data:" + dataEntity.toString());
        myBed = AppConfig.getInstance().getMyBed();
        followBed = AppConfig.getInstance().getFollowBed();
        if(AppConfig.getInstance().getMode() == 1){
            if(myBed != null){
                myBed.put("0","0");
            }

        }

        AppConfig appConfig = AppConfig.getInstance();

        SocketEntity socketEntity = null;
        for(int index = 0; index < dataEntity.d.size(); index++) {
            socketEntity = dataEntity.d.get(index);

            // 根据最新数据和本地选定的床位，提示用户
            if (followBed != null && followBed.containsKey(String.valueOf(socketEntity.BedId)) ||
                    myBed != null && myBed.containsKey(String.valueOf(socketEntity.BedId))) {
                SocketDataProcess.notificate(socketEntity.WorkingState, appConfig);
            }
            setData(socketEntity, isFollow);
        }
    }
}
