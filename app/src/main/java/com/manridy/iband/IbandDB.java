package com.manridy.iband;

import android.database.sqlite.SQLiteDatabase;


import com.manridy.iband.adapter.AppAdapter;
import com.manridy.iband.adapter.HistoryAdapter;
import com.manridy.iband.bean.AppModel;
import com.manridy.iband.bean.BoModel;
import com.manridy.iband.bean.BpModel;
import com.manridy.iband.bean.ClockModel;
import com.manridy.iband.bean.DayBean;
import com.manridy.iband.bean.HeartModel;
import com.manridy.iband.bean.SedentaryModel;
import com.manridy.iband.bean.SleepModel;
import com.manridy.iband.bean.StepModel;
import com.manridy.iband.bean.UserModel;
import com.manridy.iband.bean.ViewModel;
import com.manridy.sdk.common.TimeUtil;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据库操作
 * Created by jarLiao on 2016/10/26.
 */

public class IbandDB {
    private SQLiteDatabase db;
    private static IbandDB instance;

    private IbandDB() {
        db = Connector.getDatabase();
    }

    public static synchronized IbandDB getInstance() {
        if (instance == null) {
            instance = new IbandDB();
        }
        return instance;
    }



    /**
     * 得到用户数据
     * @return 用户数据
     */
    public UserModel getUser(){
        return DataSupport.findFirst(UserModel.class);
    }

    public List<ViewModel> getView(){
        return DataSupport.findAll(ViewModel.class);
    }

    public SedentaryModel getSedentary(){
        return DataSupport.findLast(SedentaryModel.class);
    }

    public List<ClockModel> getClock(){
        return DataSupport.findAll(ClockModel.class);
    }

    public StepModel getCurStep(){
       return DataSupport.where("stepDay = ? and stepType = ?",TimeUtil.getNowYMD(),"0").findFirst(StepModel.class);
    }


    public List<StepModel> getCurSectionStep(){
        return DataSupport.where("stepDay = ? and stepType = ?",TimeUtil.getNowYMD(),"1").find(StepModel.class);
    }

    public List<StepModel> getCurRunStep(String day){
        return DataSupport.where("stepDay = ? and stepType = ?",day,"2").find(StepModel.class);
    }

    public List<SleepModel> getCurSleeps(){
        return DataSupport.where("sleepDay = ?",TimeUtil.getNowYMD()).find(SleepModel.class);
    }

    public HeartModel getLastHeart(){
        return DataSupport.order("heartDate desc").findFirst(HeartModel.class);
    }

    public List<HeartModel> getLastsHeart(){
        return DataSupport.order("heartDate desc").limit(15).find(HeartModel.class);
    }

    public BpModel getLastBp(){
        return DataSupport.order("bpDate desc").findFirst(BpModel.class);
    }

    public List<BpModel> getLastsBp(){
        return DataSupport.order("bpDate desc").limit(7).find(BpModel.class);
    }

    public BoModel getLastBo(){
        return DataSupport.order("boDate desc").findFirst(BoModel.class);
    }

    public List<BoModel> getLastsBo(){
        return DataSupport.order("boDate desc").limit(15).find(BoModel.class);
    }

    public List<DayBean> getMonthStep(List<String> days) {
        List<DayBean> dayData = new ArrayList<>();
        for (String day : days) {
            DayBean dayBean = new DayBean(day);
            int step = DataSupport.where("stepDay = ? and stepType = ?",day,"1").sum(StepModel.class,"stepNum",int.class);
            int mi = DataSupport.where("stepDay = ? and stepType = ?",day,"1").sum(StepModel.class,"stepMileage",int.class);
            int ka = DataSupport.where("stepDay = ? and stepType = ?",day,"1").sum(StepModel.class,"stepCalorie",int.class);
            dayBean.setDaySum(step);
            dayBean.setDayMin(mi);
            dayBean.setDayMax(ka);
            dayBean.setDayCount(step == 0?0:1);
            dayData.add(dayBean);
        }
        return dayData;
    }


    public List<DayBean> getMonthSleep(List<String> days) {
        List<DayBean> dayData = new ArrayList<>();
        for (String day : days) {
            DayBean dayBean = new DayBean(day);
            dayBean.setDayMax(DataSupport.where("sleepDay = ?",day).sum(SleepModel.class,"sleepDeep",int.class));
            dayBean.setDayMin(DataSupport.where("sleepDay = ?",day).sum(SleepModel.class,"sleepLight",int.class));
            dayBean.setDayCount(DataSupport.where("sleepDay = ?",day).count(SleepModel.class));
            dayData.add(dayBean);
        }
        return dayData;
    }

    public List<HistoryAdapter.Item> getMonthHeart(List<String> days){
        List<HistoryAdapter.Item> dayData = new ArrayList<>();
        SimpleDateFormat dateFormFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateToFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        for (int i = days.size() - 1; i >= 0; i--) {
            List<HeartModel> heartModels =DataSupport.where("heartDay = ?", days.get(i)).order("heartDate desc").find(HeartModel.class);
            for (HeartModel heartModel : heartModels) {
                String time ="";
                try {
                    Date date = dateFormFormat.parse(heartModel.getHeartDate());
                    time = dateToFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HistoryAdapter.Item dayBean = new HistoryAdapter.Item(time,"",heartModel.getHeartRate()+"","次/分钟");
                dayData.add(dayBean);
            }
        }
        return dayData;
    }

    public List<HistoryAdapter.Item> getMonthBp(List<String> days){
        List<HistoryAdapter.Item> dayData = new ArrayList<>();
        SimpleDateFormat dateFormFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateToFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        for (int i = days.size() - 1; i >= 0; i--) {
            List<BpModel> bpModels =DataSupport.where("bpDay = ?",days.get(i)).order("bpDate desc").find(BpModel.class);
            for (BpModel bpModel : bpModels) {
                String time ="";
                try {
                    Date date = dateFormFormat.parse(bpModel.getBpDate());
                    time = dateToFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HistoryAdapter.Item dayBean = new HistoryAdapter.Item(time,"",bpModel.getBpHp()+"/"+bpModel.getBpLp(),"mmHg");
                dayData.add(dayBean);
            }
        }
        return dayData;
    }

    public List<HistoryAdapter.Item> getMonthBo(List<String> days){
        List<HistoryAdapter.Item> dayData = new ArrayList<>();
        SimpleDateFormat dateFormFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateToFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        for (int i = days.size() - 1; i >= 0; i--) {
            List<BoModel> boModels =DataSupport.where("boDay = ?",days.get(i)).order("boDate desc").find(BoModel.class);
            for (BoModel boModel : boModels) {
                String time ="";
                try {
                    Date date = dateFormFormat.parse(boModel.getboDate());
                    time = dateToFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HistoryAdapter.Item dayBean = new HistoryAdapter.Item(time,"",boModel.getboRate(),"%");
                dayData.add(dayBean);
            }
        }
        return dayData;
    }

    public List<StepModel> getStepList(){
        return DataSupport.findAll(StepModel.class);
    }

    public List<SleepModel> getSleepList(){
        return DataSupport.findAll(SleepModel.class);
    }

    public List<HeartModel> getHrList(){
        return DataSupport.findAll(HeartModel.class);
    }

    public List<BpModel> getBpList(){
        return DataSupport.findAll(BpModel.class);
    }

    public List<BoModel> getBoList(){
        return DataSupport.findAll(BoModel.class);
    }


    public List<AppModel> getAppList(){
        return DataSupport.findAll(AppModel.class);
    }

    public void saveAppList(List<AppAdapter.Menu> menuList){
        DataSupport.deleteAll(AppModel.class);
        for (AppAdapter.Menu menu : menuList) {
            new AppModel(menu.menuId,menu.menuName,menu.menuCheck).save();
        }
    }
}
