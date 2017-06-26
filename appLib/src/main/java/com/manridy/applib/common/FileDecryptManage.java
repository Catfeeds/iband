package com.manridy.applib.common;

import com.manridy.applib.smEncrypt.Sm4Util;
import com.manridy.applib.utils.DesUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;


/**
 * 文件加密解密管理类
 * Created by jarLiao on 2016/8/9.
 */  
public class FileDecryptManage {
    /**
     * 加密监听
     */
    static EncProgressListener Enclistener;
    /**
     * 解密监听
     */
    static DecProgressListener Declistener;

    private FileDecryptManage() {
    }

    private static FileDecryptManage instance = null;

    public static FileDecryptManage getInstance() {
        synchronized (FileDecryptManage.class) {
            if (instance == null)  
                instance = new FileDecryptManage();
        }  
        return instance;  
    }

    public  interface EncProgressListener{
        /**
         * 得到加密进度
         * @param num 进度
         */
        void getEncProgress(long num);
    }
    public  interface DecProgressListener {
        /**
         * 得到解密进度
         * @param num
         */
        void getDecProgress(long num);
    }

    public void setEncListener(EncProgressListener listener){
        this.Enclistener = listener;
    }

    public void setDecListener(DecProgressListener listener){
        this.Declistener = listener;
    }

    /**
     * DES加密文件
     * @param key 加密密匙
     * @param srcFilePath 需要加密文件路径
     * @param enctionFilePath 加密完成文件路径
     * @throws Exception
     */
    public static boolean encryptionFile(byte[] key, String srcFilePath, String enctionFilePath) throws Exception {
        int len;
        boolean end = false;
        int loadNum =0;//已完成计数
        long srcLenght;//源文件大小
        Cipher cipher = null;//加密对象
        byte[] cipherBuffer;//加密数据
        byte[] buffer = new byte[100*1024];//缓存

        //初始化流对象
        File srcFile=new File(srcFilePath);
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(new File(enctionFilePath));
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        srcLenght = srcFile.length();


        //文件加密操作
        while ((len = bis.read(buffer))!= -1){//读取文件流
            cipher = DesUtil.encryptToFile(key);//得到加密对象
            cipherBuffer = cipher.update(buffer,0,len);//对读取的文件流进行加密
            bos.write(cipherBuffer);//加密数据写入文件
            bos.flush();//刷新缓存
            loadNum = len +loadNum;//统计已加密数据大小
            Enclistener.getEncProgress((long) ((double)loadNum/(double)srcLenght*100));//返回加密进度
        }

//        //结束加密
        if (cipher != null) {
            cipherBuffer = cipher.doFinal();
            bos.write(cipherBuffer);
            bos.flush();
            end = true;
        }

        //关闭文件流
        if (fos != null)
            fos.close();
        if (fis != null)
            fis.close();
        if (bos!=null)
            bos.close();
        if (bis!= null)
            bis.close();
        return end;
    }

    /**
     * DES解密文件
     * @param key 密匙
     * @param srcFilePath 源文件路径
     * @param dectionFilePath 解密后文件路径
     * @throws Exception
     */
    public static boolean decryptionFile(byte[] key, String srcFilePath, String dectionFilePath) throws IOException,Exception {
        int len;
        boolean end = false;
        long loadNum = 0;//已完成计数
        long srcLenght;//源文件大小
        Cipher cipher = null;//解密对象
        byte[] plainbuffer;//解密数据
        byte[] buffer = new byte[100*1024];//缓存

        //初始化流对象
        File srcFile =new File(srcFilePath);
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(new File(dectionFilePath));
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        srcLenght = srcFile.length();


        //文件解密操作
        while ((len = bis.read(buffer)) != -1){//读取加密文件流
            cipher = DesUtil.decryptToFile(key);//得到文件解密对象
            plainbuffer = cipher.update(buffer,0,len);//对加密文件流解密操作
            bos.write(plainbuffer);//解密数据写入文件
            bos.flush();//刷新缓存
            loadNum = loadNum +len;//统计已解密
            Declistener.getDecProgress((long) ((double)loadNum/(double)srcLenght*100));
        }
        //结束加密
        if (cipher!=null) {
            plainbuffer = cipher.doFinal();
            bos.write(plainbuffer);
            bos.flush();
            end = true;
        }

        //关闭文件流
        if(fis!=null)
            fis.close();
        if(fos!=null)
            fos.close();
        if (bos!=null)
            bos.close();
        if (bis!= null)
            bis.close();
        return end;
    }

    /**
     * SM4加密文件
     * 头部写入8字节加密标识
     * 只加密头部64K
     * @param key 密匙
     * @param srcFilePath 源文件
     * @param enctionFilePath 加密后存储地址
     * @return
     * @throws Exception
     */
    public static boolean encryptionSm4File(byte[] key, String srcFilePath, String enctionFilePath) throws Exception {
        int len;
        boolean end = false;
        int loadNum =0;//已完成计数
        long srcLenght;//源文件大小
        byte[] cipherBuffer;//加密数据
        byte[] buffer = new byte[100*1024];//缓存
        int blockSize = 64 *1024;

        //初始化流对象
        File srcFile=new File(srcFilePath);
        srcLenght = srcFile.length();
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(new File(enctionFilePath));
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        //文件加密操作
        if (srcLenght < blockSize) {//如果小于64K
            byte[] inBytes = new byte[(int) srcLenght];
            //向文件写入已加密的标示内容，可自行定义内容，与判断时一致即可
            bos.write("^%!_$#@*".getBytes());
            //一次性读取文件的全部内容
            if ((len = bis.read(inBytes)) != -1){
                //调用加密的接口传入内容参数
                cipherBuffer= Sm4Util.encodeSMS4(inBytes,key);
                //加密后的内容写入文件中
                bos.write(cipherBuffer);
                bos.flush();//刷新缓存
                Enclistener.getEncProgress(100);
            }
        }else{//如果大于64k
            //以加密处理
            byte[] inBytes = new byte[blockSize];
            //向文件写入已加密的标示内容，可自行定义内容，与判断时一致即可
            bos.write("^%!_$#@*".getBytes());
            bis.read(inBytes);
            cipherBuffer= Sm4Util.encodeSMS4(inBytes,key);//加密头
            bos.write(cipherBuffer,0,cipherBuffer.length);
            bos.flush();//刷新缓存
            //未加密处理
            while ((len = bis.read(buffer))!= -1) {//读取文件流
                bos.write(buffer);//加密数据写入文件
                bos.flush();//刷新缓存
                loadNum = len +loadNum;//统计已加密数据大小
                Enclistener.getEncProgress((long) ((double)loadNum/((double)srcLenght-blockSize-8)*100));//返回加密进度
            }
        }

        //关闭文件流
        if (fos != null)
            fos.close();
        if (fis != null)
            fis.close();
        if (bos!=null)
            bos.close();
        if (bis!= null)
            bis.close();
        return end;
    }

    /**
     * SM4解密文件
     * 删除头部8字节标识
     * 只加密头部64K
     * @param key 密匙
     * @param srcFilePath 源文件
     * @param dectionFilePath 解密后文件存储地址
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static boolean decryptionSm4File(byte[] key, String srcFilePath, String dectionFilePath) throws IOException,Exception {
        int len;
        boolean end = false;
        long loadNum = 0;//已完成计数
        long srcLenght;//源文件大小
        byte[] plainbuffer;//解密数据
        byte[] buffer = new byte[100*1024];//缓存
        int blockSize = 64 *1024;


        //初始化流对象
        File srcFile =new File(srcFilePath);
        srcLenght = srcFile.length();
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(new File(dectionFilePath));
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        if (srcLenght < blockSize){//小于64kb解密
            //跳过加密标识符
            bis.skip(8);
            //重新获取大小
            srcLenght = bis.available();
            byte[] inBytes = new byte[(int) srcLenght];
            if ((len = bis.read(inBytes))!= -1){
                plainbuffer = Sm4Util.decodeSMS4(inBytes,key);
                bos.write(plainbuffer);
                bos.flush();
                Declistener.getDecProgress(100);
            }
        }else{//大于64kb解密
            bis.skip(8);
            //重新获取大小
            srcLenght = bis.available();
            byte[] inBytes = new byte[(int) blockSize];
            if ((len = bis.read(inBytes))!= -1){
                plainbuffer = Sm4Util.decodeSMS4(inBytes,key);
                bos.write(plainbuffer,0,plainbuffer.length);
                bos.flush();
            }
            while ((len = bis.read(buffer))!= -1) {//读取文件流
                bos.write(buffer);
                bos.flush();//刷新缓存
                loadNum = len +loadNum;//统计
                Declistener.getDecProgress((long) ((double)loadNum/((double)srcLenght-blockSize-8)*100));//返回
            }
        }



        //关闭文件流
        if(fis!=null)
            fis.close();
        if(fos!=null)
            fos.close();
        if (bos!=null)
            bos.close();
        if (bis!= null)
            bis.close();
        return end;
    }


}