package com.manridy.applib.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 字节处理工具类
 * Created by jarLiao on 2016/7/27.
 */
public class BitUtil {

    /**
     * 将byte转换为一个长度为8的byte数组,数组每个值代表bit
     *
     * @param b
     * @return
     */
    public static byte[] getBitArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 二进制字符串转byte
     * 00000000
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * String转BCD编码函数
     *
     * @param asc
     * @return
     */
    public static byte[] str2Bcd(String asc,boolean first) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            if (first) {
                asc = "0" + asc;
            }else {
                asc = asc + "0";
            }
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * BCD转string编码函数
     *
     * @param bytes
     * @return
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
    }

    /**
     * 得到消息类型
     *
     * @param bits
     * @return
     */
    public static int getInfoType(byte[] bits) {
        int num;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < 8; i++) {
            stringBuilder.append(bits[i]);
        }
        num = Integer.parseInt(stringBuilder.toString(), 2);
        return num;
    }

    /**
     * 打印bit字符串
     *
     * @param b
     * @return 00000000
     */
    public static String bitToString(byte b) {
        byte[] bits = getBitArray(b);
        StringBuilder builder = new StringBuilder();
        for (byte bit : bits) {
            builder.append(bit);
        }
        return builder.toString();
    }

    /**
     * byte数组转化成int
     *  解析多个字节代表的数据
     *  转化成bit 拼接 然后2进制转化
     * @param bytes
     * @return 数值
     */
    public static int byte3ToInt(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            builder.append(bitToString(bytes[i]));
        }
        return Integer.parseInt(builder.toString(), 2);
    }

    /**
     * 解析bits数组转化成int
     * 拼接bit然后转2进制
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        return Integer.parseInt(bitToString(b).toString(), 2);
    }

    /**
     * 单个byteBCD格式转换字符串
     *
     * @param b
     * @return
     */
    public static String byteBcd2Str(byte b) {
        byte[] bs = new byte[2];
        bs[1] = b;
        return bcd2Str(bs).substring(1);
    }

    /**
     * 时间byte数据处理
     * @param date 数据源
     * @param type 0 6字节byte YY-MM-DD HH:mm:ss
     *              1 5字节byte YY-MM-DD HH:MM
     *              2 5字节byte MM-DD HH:mm:ss
     *              3 2字节byte YY-MM-DD
     *              4 3字节byte YY-MM-DD
     * @return
     */
    public static String bytesToDate(byte[] date, int type) {
        int[] dates = new int[date.length];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat =new SimpleDateFormat();
        for (int i = 0; i < date.length; i++) {//BCDbyte转换
            dates[i] = Integer.parseInt(byteBcd2Str(date[i]));
        }
        switch (type) {//格式转换 时间填充
            case 0:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                calendar.set(dates[0]+2000,dates[1]-1,dates[2],dates[3],dates[4],dates[5]);
                break;
            case 1:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                calendar.set(dates[0]+2000,dates[1]-1,dates[2],dates[3],dates[4]);
                break;
            case 2:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                calendar.set(calendar.get(Calendar.YEAR),dates[0]-1,dates[1],dates[2],dates[3],dates[4]);
                break;
            case 3:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                calendar.set(calendar.get(Calendar.YEAR),dates[0]-1,dates[1]);
                break;
            case 4:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                calendar.set(dates[0]+2000,dates[1]-1,dates[2]);
                break;
        }
        String d =dateFormat.format(calendar.getTime());
        return d;
    }


    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 3];
        l &= 0xff;
        l |= ((long) b[index + 2] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 1] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 0] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * int转换byte数组
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static long bytesToLong(byte[] b){
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            temp.append(bitToString(b[i]));
        }
        return Long.parseLong(temp.toString(), 2);
    }

    /**
     * @Title: parseByte2HexStr
     * @Description: byte转换成16进制
     * @param @param buf
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase(Locale.US)+" ");
        }
        return sb.toString();
    }

    /**
     * @Title: parseHexStr2Byte
     * @Description: 十六进制转换成byte
     * @param @param hexStr
     * @param @return 设定文件
     * @return byte[] 返回类型
     * @throws
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public static String printBytes(byte[] bs){
        StringBuilder builder = new StringBuilder();
        for (byte b : bs) {
            builder.append(b+" ");
        }
        return builder.toString();
    }
}