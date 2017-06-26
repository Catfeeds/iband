package com.manridy.applib.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Des加密类
 * Created by jarLiao on 2016/6/29.
 */
public class DesUtil {

    private static String DES = "DES";

    /**
     * Description 根据键值进行加密
     * @param data 加密字符
     * @param key 密匙字符
     * @return 加密键byte数组
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        return new String(bt);
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data 解密字符
     * @param key 密匙字符
     * @return 加密键byte数组
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws Exception {
        if (data == null)
            return null;
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(data.getBytes(), key.getBytes());
        return new String(bt);
    }

    /**
     * Description 根据键值进行加密
     * @param data 字节数据
     * @param key 字节密匙
     * @return 加密键byte数组
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }


    /**
     * 加密文件
     * @param key 密匙
     * @return cipher 加密对象
     * @throws Exception
     */
    public static Cipher encryptToFile(byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

        return cipher;
    }

    /**
     * 解密文件
     * @param key
     * @return cipher对象
     * @throws Exception
     */
    public static Cipher decryptToFile(byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

        return cipher;
    }


    /**
     * Description 根据键值进行解密
     * @param data 解密数据byte数组
     * @param key  密匙byte数组
     * @return 解密数据
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

        return cipher.doFinal(data);
    }

    /**
     * byte数组转十六进制字符串
     * @param buffer
     * @return
     */
    public static String byteToHex(byte[] buffer) {
        String h = "";
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + "" + temp;
        }
        return h;
    }
}
