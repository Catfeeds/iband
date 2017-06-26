package com.manridy.sdk.ble;

/**
 * 蓝牙协议工厂
 */
public class BleProtocol {
    private static byte crc;//校验码
    private static byte[] data = new byte[19];

    //生成协议验证码
    private static byte getCrc(byte head, byte type, byte[] body){
        int d = 0 ;
        data[0] = head;
        data[1] = type;
        System.arraycopy(body,0,data,2,body.length);
        for (int i = 0; i < data.length; i++) {
            d += data[i];
        }
        crc = (byte) (((~d)+1)&0xff);
        return crc;
    }

    //生成协议完整命令
    public static byte[] cmd(byte head, byte type, byte[] body){
        byte[] cmd = new byte[20];
        cmd[cmd.length-1] = getCrc(head, type, body);
        System.arraycopy(data,0,cmd,0,data.length);
        return cmd;
    }

}
