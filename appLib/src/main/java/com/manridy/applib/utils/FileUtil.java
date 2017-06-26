package com.manridy.applib.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * File工具类
 * Created by jarLiao on 2016/6/29.
 */
 public  class FileUtil {
    public static final int FILE_IMG = 10001;
    public static final int FILE_AUDIO = 10002;
    public static final int FILE_VIDEO = 10003;
    public static final int FILE_DOC = 10004;
    public static final int FILE_ZIP = 10005;
    public static final int FILE_UNKNOW = 10006;

    /**
     * 得到文件类型
     * @param path　文件地址
     * @return　
     */
    public static int getFileType(String path){
        int fileType = FILE_UNKNOW;
        File file = new File(path);
        if (file.getName().endsWith(".jpg")
                || file.getName().endsWith(".png")
                || file.getName().endsWith(".pdf")
                || file.getName().endsWith(".pic")
                || file.getName().endsWith(".gif")
                || file.getName().endsWith(".jpeg")) {
            fileType = FILE_IMG;
        }else if (file.getName().endsWith(".mp3")
                || file.getName().endsWith(".wav")
                ||file.getName().endsWith(".aif")
                ||file.getName().endsWith(".au")
                ||file.getName().endsWith(".wma")
                ||file.getName().endsWith(".mmf")
                ||file.getName().endsWith(".arm")
                ||file.getName().endsWith(".aac")
                ||file.getName().endsWith(".fiac")){
            fileType = FILE_AUDIO;
        }else if (file.getName().endsWith(".mp4")
                || file.getName().endsWith(".avi")
                || file.getName().endsWith(".mpg")
                || file.getName().endsWith(".swf")) {
            fileType = FILE_VIDEO;
        }else if (file.getName().endsWith(".tex")
                || file.getName().endsWith(".pdf")
                ||file.getName().endsWith(".doc")
                ||file.getName().endsWith(".log")
                ||file.getName().endsWith(".html")
                ||file.getName().endsWith(".xls")
                ||file.getName().endsWith(".ppt")
                ||file.getName().endsWith(".docx")
                ||file.getName().endsWith(".xml")) {
            fileType = FILE_DOC;
        }else if (file.getName().endsWith(".zip")
                || file.getName().endsWith(".rar")
                || file.getName().endsWith(".z")
                || file.getName().endsWith(".arj")
                || file.getName().endsWith(".gz")) {
            fileType = FILE_ZIP;
        }
        return fileType;
    }

    /**
     * 得到SD卡路径
     * @return
     */
    public static String getSdCardPath(){
       return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 文件是否存在
     * @param path
     * @return
     */
    public static boolean fileExist(String path){
        return  new File(path).exists();
    }

    /**
     * 在指定路径创建文件夹
     * @param dirPath 路径
     * @return 创建结果
     */
    public static boolean mkDir(String dirPath){
        return new File(dirPath).mkdirs();
    }

    /**
     * 删除指定的文件
     *
     * @param filePath 文件路径
     *
     * @return 若删除成功，则返回True；反之，则返回False
     *
     */
    public static boolean delFile(String filePath) {
        File file =  new File(filePath);
        boolean suc = false;
        if ( file.exists()) {
          suc =  file.delete();
        }
        return suc;
    }

    /**
     * 删除文件夹下的文件
     * @param filesPath
     * @return 删除数量
     */
    public static int delFiles(String filesPath){
        File file = new File(filesPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            int delCount = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].delete()) {
                    delCount++;
                }
            }
            return delCount;
        }
        return 0;
    }

    /**
     * 删除指定的文件夹
     *
     * @param dirPath 文件夹路径
     * @param delFile 文件夹中是否包含文件
     * @return 若删除成功，则返回True；反之，则返回False
     *
     */
    public static boolean delDir(String dirPath, boolean delFile) {
        if (delFile) {
            File file = new File(dirPath);
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                if (file.listFiles().length == 0) {
                    return file.delete();
                } else {
                    int zfiles = file.listFiles().length;
                    File[] delfile = file.listFiles();
                    for (int i = 0; i < zfiles; i++) {
                        if (delfile[i].isDirectory()) {
                            delDir(delfile[i].getAbsolutePath(), true);
                        }
                        delfile[i].delete();
                    }
                    return file.delete();
                }
            } else {
                return false;
            }
        } else {
            return new File(dirPath).delete();
        }
    }

    /**
     * 复制文件/文件夹 若要进行文件夹复制，请勿将目标文件夹置于源文件夹中
     * @param source 源文件（夹）
     * @param target 目标文件（夹）
     * @param isFolder 若进行文件夹复制，则为True；反之为False
     * @throws Exception
     */
    public static void copy(String source, String target, boolean isFolder)
            throws Exception {
        if (isFolder) {
            (new File(target)).mkdirs();
            File a = new File(source);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (source.endsWith(File.separator)) {
                    temp = new File(source + file[i]);
                } else {
                    temp = new File(source + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(target + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copy(source + "/" + file[i], target + "/" + file[i], true);
                }
            }
        } else {
            int byteread = 0;
            File oldfile = new File(source);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(source);
                File file = new File(target);
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileOutputStream fs = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        }
    }

    /**
     * 移动指定的文件（夹）到目标文件（夹）
     * @param source 源文件（夹）
     * @param target 目标文件（夹）
     * @param isFolder 若为文件夹，则为True；反之为False
     * @return
     * @throws Exception
     */
    public static boolean move(String source, String target, boolean isFolder)
            throws Exception {
        copy(source, target, isFolder);
        if (isFolder) {
            return delDir(source, true);
        } else {
            return delFile(source);
        }
    }


    /**
     * 得到除掉后缀的名称
     * @param originalFileName
     * @return
     */
    public static String getFileName(String originalFileName){
        String name = "";
        if (originalFileName.lastIndexOf(".")!=-1) {
            name = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        }else{
            name =originalFileName;
        }
        return name;
    }

    /**
     * 重命名
     * @param file
     * @param toFile
     */
    public void renameFile(String file, String toFile) {

        File toBeRenamed = new File(file);
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {

            System.out.println("File does not exist: " + file);
            return;
        }

        File newFile = new File(toFile);

        //修改文件名
        if (toBeRenamed.renameTo(newFile)) {
            System.out.println("File has been renamed.");
        } else {
            System.out.println("Error renmaing file");
        }

    }

    /**
     * 得到指定路径下的所有文件
     * @param path
     * @return
     */
    public static List<File> getFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile()) {
                fileList.add(file);
            } else  {
               getFiles(file.getPath());
            }
        }
        return fileList;
    }

    /**
     * 得到文件大小
     * @param size
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
}