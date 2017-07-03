package com.manridy.sdk.ble;


import com.manridy.sdk.common.BitUtil;
import com.manridy.sdk.common.TimeUtil;

import java.io.UnsupportedEncodingException;

/**
 * 蓝牙协议封装
 */
public class BleCmd {
    static byte head = (byte) 0xFC;//消息头
    static byte type;//消息类型
    static byte[] body;//消息体

    /**
     * 设置当前时间
     * @return
     */
    public static byte[] setTime(){
        type = 0x00;
        body= new byte[17];
        int[] time = TimeUtil.getNowYMDHMStoInt();
        byte[] bcd = new byte[time.length];
        time[0] = time[0]%1000;
        for (int i = 0; i < time.length; i++) {
            bcd[i] =  BitUtil.str2Bcd(String.valueOf(time[i]),true)[0];
        }
        System.arraycopy(bcd,0,body,0,bcd.length);
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置闹钟
     * @param data 闹钟数据[h][m]
     * @param onOff 0关，1开
     * @return
     */
    public static byte[] setAlarm(String[] data,int[] onOff ){
        type = 0x01;
        body= new byte[17];
        body[0] = 0x00;
        //计算开关
        for (int i = 0; i < onOff.length; i++) {
            body[i+1] = (byte) onOff[i];
        }
        //计算闹钟
        for (int i = 0; i < data.length; i++) {
            if (!data[i].equals("")) {
                String[] t = data[i].split(":");
                body[(2*i)+6] = BitUtil.str2Bcd(t[0],true)[0];
                body[(2*i)+7] = BitUtil.str2Bcd(t[1],true)[0];
            }
        }
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 得到闹钟
     * @return
     */
    public static byte[] getAlarm(){
        type = (byte) 0x01;
        body= new byte[17];
        body[0] = (byte) 0x01;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置震动开关
     * @param onOff 0关，1开
     * @return
     */
    public static byte[] setShake(int onOff){
        type = 0x02;
        body= new byte[17];
        body[0] = (byte) onOff;
        return BleProtocol.cmd(head,type,body);
    }


    /**
     * 获得运动数据
     * @param i 0 步数，1 里程，2 卡路里，3全部
     * @return
     */
    public static byte[] getSport(int i){
        type = 0x03;
        body= new byte[17];
        String en ="00000";
        switch (i){
            case 0:
                en += "001";
                break;
            case 1:
                en += "010";
                break;
            case 2:
                en += "100";
                break;
            case 3:
                en += "111";
        }
        body[0] = BitUtil.decodeBinaryString(en);
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取计步历史条数
     * @return
     */
    public static byte[] getSportHistoryNum(){
        type = 0x03;
        body= new byte[17];
        body[0] = (byte) 0x80;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取计步历史数据
     * @return
     */
    public static byte[] getSportHistoryData(){
        type = 0x03;
        body= new byte[17];
        body[0] = (byte) 0xC0;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 确认计步历史数据
     * @param num
     * @return
     */
    public static byte[] confirmSportHistoryData(int num){
        type = 0x03;
        body= new byte[17];
        body[0] = (byte) 0xC0;
        body[1] = (byte) (num >> 8 & 0xff);
        body[2] = (byte) (num & 0xff);
        return BleProtocol.cmd(head,type,body);
    }
    /**
     * 清零运动数据
     * @return
     */
    public static byte[] clearSport(){
        type = 0x04;
        body= new byte[17];
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取GPS数据
     * @return
     */
    public static byte[] getGps(){
        type = 0x05;
        body= new byte[17];
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 推送用户信息
     * @return
     */
    public static byte[] setUserInfo(int height,int weight){
        type = 0x06;
        body= new byte[17];
        body[0] = (byte) weight;
        body[1] = (byte) height;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置运动目标
     * @return
     */
    public static byte[] setSportTarget(int target){
        type = 0x07;
        body= new byte[17];
        byte[] bs = BitUtil.intToByteArray(target);
        System.arraycopy(bs,1,body,1,3);
        body[0] = 1;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 消息推送
     * 00 来电，01 短信，02，应用消息
     * @return
     */
    public static byte[] setInfoAlert(int infoType){
        type = 0x08;
        body= new byte[17];
        body[0] = (byte) infoType;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置来电提醒
     * @param id
     * @param str
     * @return
     */
    public static byte[] setPhoneAlert(int id,String str){
        type = 0x08;
        body= new byte[17];
        body[0] = 0;
        body[1] = (byte) id;
        if (id == 1){//发送来电联系人名称
            try {
                byte[] names = str.getBytes("UnicodeBigUnmarked");//string转uicode编码 大端在前
                body[2] = (byte) names.length;
                System.arraycopy(names,0,body,3,names.length>14?14:names.length);//最多截取14位
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if (id == 2){//发送来电号码
            byte[] nums = BitUtil.str2Bcd(str,false);
            body[2] = (byte) str.length();
            System.arraycopy(nums,0,body,3,nums.length>14?14:nums.length);
        }
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置短信提醒名称
     * @param id
     * @param ty
     * @param name
     * @return
     */
    public static byte[] setSmsAlertName(int id,int ty ,String name){
        type = 0x08;
        body= new byte[17];
        body[0] = 1;
        body[1] = (byte) (id | 1 << 7 | ty << 6);
        if (ty == 1) {//名称
            body[3] = 0x03;//字符集 unicode编码
            try {
                byte[] names = name.getBytes("UnicodeBigUnmarked");//string转uicode编码 大端在前
                body[2] = (byte) names.length;
                System.arraycopy(names,0,body,4,names.length>12?12:names.length);//最多截取14位
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if (ty == 0){//号码
            body[3] = 0x00;//字符集 ASCII BCD
            byte[] nums = BitUtil.str2Bcd(name,false);
            body[2] = (byte) name.length();
            System.arraycopy(nums,0,body,4,nums.length>12?12:nums.length);//最多截取14位
        }
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置短信提醒内容
     * @param id
     * @param context
     * @return
     */
    public static byte[] setSmsAlertContext(int id,byte[] context){
        type = 0x08;
        body= new byte[17];
        body[0] = 1;//短信标识
        body[1] = (byte) (id | 0<<7);//短信id与内容
        body[2] = (byte) context.length;//总长度
        body[3] = 0x03;//字符集 unicode编码
        System.arraycopy(context,0,body,4,context.length);//最多截取14位
        return BleProtocol.cmd(head,type,body);
    }

    public static byte[] setAppAlertName(int id,int ty){
        type = 0x08;
        body= new byte[17];
        body[0] = (byte) ty;
        body[1] = (byte) (id | 1 << 7 | 1 << 6);
        body[3] = 0x03;//字符集 unicode编码
        try {
            byte[] names = "QQ".getBytes("UnicodeBigUnmarked");//string转uicode编码 大端在前
            body[2] = (byte) names.length;
            System.arraycopy(names,0,body,4,names.length>12?12:names.length);//最多截取14位
        } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
        }
        return BleProtocol.cmd(head,type,body);
    }

    public static byte[] setAppAlertContext(int id,int ty,byte[] context){
        type = 0x08;
        body= new byte[17];
        body[0] = (byte) ty;//标识
        body[1] = (byte) (id | 0<<7);//短信id与内容
        body[2] = (byte) context.length;//总长度
        body[3] = 0x03;//字符集 unicode编码
        System.arraycopy(context,0,body,4,context.length);//最多截取14位
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 心率测试
     * 00 停止, 01 开始
     * @return
     */
    public static byte[] setHrTest(int onOff){
        type = 0x09;
        body= new byte[17];
        body[0] = (byte) onOff;
        return BleProtocol.cmd(head,type,body);
    }



    /**
     * 获取心率数据
     * 00 最近一次, 01 历史心率
     * @return
     */
    public static byte[] getHrData(int dateType){
        type = 0x0A;
        body= new byte[17];
        body[0] = (byte) dateType;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取睡眠数据
     * @param dateType 00最近一次 01历史数据
     * @return
     */
    public static byte[] getSleep(int dateType){
        type = (byte) 0x0c;
        body= new byte[17];
        body[0] = (byte) dateType;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 确认睡眠历史数据
     * @param num
     * @return
     */
    public static byte[] confirmSleep(int num){
        type = (byte) 0x0c;
        body= new byte[17];
        body[0] = 0x01;
        body[1] = (byte) (num >> 8 & 0xff);
        body[2] = (byte) (num & 0xff);
        return BleProtocol.cmd(head,type,body);
    }


    /**
     * 查找设备
     * @param dateType 00取消  01震动 02屏幕抖动 03 震动并抖动
     * @return
     */
    public static byte[] findDevice(int dateType){
        type = (byte) 0x10;
        body= new byte[17];
        body[1] = (byte) dateType;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 确认设备找到手机
     * @return
     */
    public static byte[] affirmFind(){
        //特殊协议
        return BitUtil.parseHexStr2Byte("1001000000000000000000000000000000000000");
    }

    /**
     * 得到血压
     * @param dateType 00最近一次 01历史数据 02获取历史数目
     * @return
     */
    public static byte[] getBloodpPressure(int dateType){
        type = (byte) 0x11;
        body= new byte[17];
        body[0] = (byte) dateType;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 得到血氧
     * @param dateType 00最近一次 01历史数据 02获取历史数目
     * @return
     */
    public static byte[] getBloodOxygen(int dateType){
        type = (byte) 0x12;
        body= new byte[17];
        body[0] = (byte) dateType;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 固件版本号
     * @return
     */
    public static byte[] getFirmware(){
        type = (byte) 0x0F;
        body= new byte[17];
        body[0] = 0x05;
        return BleProtocol.cmd(head,type,body);
    }


    /**
     * 得到电池电量
     * @return
     */
    public static byte[] getBattery(){
        type = (byte) 0x0F;
        body= new byte[17];
        body[0] = 0x06;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 防丢提醒
     * @param onOff 开关
     * @param timeout 延时时间
     * @return
     */
    public static byte[] setLostDeviceAlert(int onOff,int timeout){
        type = (byte) 0x10;
        body= new byte[17];
        body[0] = 0x02;
        body[1] = (byte) onOff;
        body[2] = (byte) timeout;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 自动测量心率
     * @param onOff 0 关闭 1 开启
     * @return
     */
    public static byte[] setHrAuto(int onOff){
        type = (byte) 0x09;
        body= new byte[17];
        body[0] = 0x01;
        body[1] = (byte) onOff;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 动/静态心率
     * @param onOff 0 静态 1 动态
     * @return
     */
    public static byte[] setHrTest2(int onOff){
        type = (byte) 0x09;
        body= new byte[17];
        body[2] = 0x01;
        body[3] = (byte) onOff;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 久坐提醒
     * @param
     * @return
     */
    public static byte[] setSedentaryAlert(int alertOnOff,int disturbOnOff,int space,int steps,String[] times){
        type = (byte) 0x16;
        body= new byte[17];
        body[0] = (byte) (alertOnOff | (disturbOnOff<<1));
        byte[] alertSpace = new byte[]{
                (byte) ((space >> 8) & 0xFF),
                (byte) (space & 0xFF)};
        byte[] alertSteps = new byte[]{
                (byte) ((steps >> 8) & 0xFF),
                (byte) (steps & 0xFF)};
        System.arraycopy(alertSpace,0,body,1,alertSpace.length);
        System.arraycopy(alertSteps,0,body,11,alertSteps.length);

        //计算闹钟
        for (int i = 0; i < times.length; i++) {
            if (!times[i].equals("")) {
                String[] t = times[i].split(":");
                body[(2*i)+3] = BitUtil.str2Bcd(t[0],true)[0];
                body[(2*i)+4] = BitUtil.str2Bcd(t[1],true)[0];
            }
        }
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置设备名称
     * @param name 名称
     * @return
     */
    public static byte[] setDeviceName(String name){
        try {
            type = (byte)0x0F;
            body= new byte[17];
            body[0] = 07 ;
            byte[] b_name = name.getBytes("UTF-8");
            body[1] = (byte) b_name.length;
            System.arraycopy(b_name,0,body,2,b_name.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置设备单位
     * @param unit 0为公制 1为英制
     * @return
     */
    public static byte[] setUnit(int unit){
        type = (byte) 0x17;
        body= new byte[17];
        body[1] = (byte) unit;
        return BleProtocol.cmd(head,type,body);
    }


    /**
     * 提取设备日志
     * @param ty 日志类型 0关闭/1打开/2提取
     * @return
     */
    public static byte[] getLog(int ty){
        type = (byte) 0x0F;
        body= new byte[17];
        body[0] = 0x0A;
        body[1] = (byte) ty;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设备lcd亮度控制
     * @param num 0-255
     * @return
     */
    public static byte[] setLight(int num){
        type = 0x0F;
        body = new byte[17];
        body[0] = 0x04;
        body[1] = (byte) num;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 蓝牙名称恢复出厂
     * @return
     */
    public static byte[] resetName(){
        type = 0x0F;
        body = new byte[17];
        body[0] = 0x08;
        return BleProtocol.cmd(head,type,body);
    }


    /**
     * 设备蓝牙界面控制
     * @param viewOnOff 1:进入拍照，0:退出拍照
     * @return
     */
    public static byte[] setCameraViewOnOff(int viewOnOff){
        type = 0x19;
        body = new byte[17];
        body[0] = (byte) (1 << 7 | viewOnOff);
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设备蓝牙控制
     * @param notify 1:开始拍照，0:拍照完成
     * @return
     */
    public static byte[] setCameraNotify(int notify){
        type = 0x19;
        body = new byte[17];
        body[1] = (byte) (1 << 7 | notify);
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取分段计步历史
     * @return
     */
    public static byte[] getStepSectionHistroy(){
        type = 0x1A;
        body = new byte[17];
        body[0] = 1 << 2;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取分段计步长度
     * @return
     */
    public static byte[] getStepSectionNum(){
        type = 0x1A;
        body = new byte[17];
        body[0] = 1 << 1;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取跑步当前
     * @return
     */
    public static byte[] getRunCurrent(){
        type = 0x1B;
        body = new byte[17];
        body[0] = 1 << 3;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取跑步历史数据
     * @return
     */
    public static byte[] getRunHistoryData(){
        type = 0x1B;
        body = new byte[17];
        body[0] = 1 << 2;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取跑步历史数量
     * @return
     */
    public static byte[] getRunHistoryNum(){
        type = 0x1B;
        body = new byte[17];
        body[0] = 1 << 1;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取窗口数量
     * @return
     */
    public static byte[] getWindowsNum(){
        type = 0x1C;
        body = new byte[17];
        body[0] = 1;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取窗口设置
     * @param
     * @return
     */
    public static byte[] getWindowsSet(int[] ids,int[] onOffs){
        type = 0x1C;
        body = new byte[17];
        body[0] = 1 << 1;
        body[1] = 1;
        StringBuilder builder = new StringBuilder();
        for (int i = onOffs.length - 1; i >= 0; i--) {//倒序取出
            builder.append(onOffs[i]);
        }
        int eh = Integer.parseInt(builder.toString(),2);
        body[2] = (byte) ((eh >> 8) & 0xFF);
        body[3] = (byte) (eh & 0xFF);
        for (int i = 0; i < ids.length; i++) {
            body[i+4] = (byte) (ids[i] |0x80);
        }
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 获取窗口关系
     * @return
     */
    public static byte[] getWindowsChild(int st){
        type = 0x1C;
        body = new byte[17];
        body[0] = 1 << 2;
        body[1] = (byte) st;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 24小时或12小时切换
     * 0 24小时 1 12小时
     * @return
     */
    public static byte[] setHourSelect(int ss){
        type = 0x18;
        body = new byte[17];
        body[1] = (byte) ss;
        return BleProtocol.cmd(head,type,body);
    }

    /**
     * 设置翻腕亮屏开关
     * @param onOff 0关 1开
     * @return
     */
    public static byte[] setWristOnOff(int onOff){
        type = 0x15;
        body = new byte[17];
        body[0] = (byte) onOff;
        return BleProtocol.cmd(head,type,body);
    }
}
