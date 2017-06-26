package com.manridy.iband.view.setting;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.R;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class CameraActivity extends BaseActionActivity {

    @BindView(R.id.iv_camera_start)
    ImageView ivCameraStart;
    @BindView(R.id.sv_camera)
    SurfaceView svCamera;
    @BindView(R.id.iv_capture)
    ImageView ivCapture;

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isBackCameraOn = true;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("遥控拍照");
    }

    @Override
    protected void initVariables() {
        registerEventBus();
    }

    @Override
    protected void initListener() {
        ivCameraStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svCamera.setVisibility(View.VISIBLE);
                ivCapture.setVisibility(View.VISIBLE);
                mIwaerApplication.service.watch.sendCmd(BleCmd.setCameraViewOnOff(1), new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onFailure(BleException exception) {

                    }
                });
                mSurfaceHolder = svCamera.getHolder();
                mSurfaceHolder.addCallback(SurfaceHolderCallback);
            }
        });

        ivCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        svCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
    }


    SurfaceHolder.Callback SurfaceHolderCallback=  new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            setStartPreview(mCamera, mSurfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setStartPreview(mCamera, mSurfaceHolder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCamera();
        }
    };

    /**
     * 初始化相机
     * @return camera
     */
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = Camera.open(Camera.getNumberOfCameras()-1);
        }
        return camera;
    }

    /**
     * 检查是否具有相机功能
     * @param context context
     * @return 是否具有相机功能
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * 在SurfaceView中预览相机内容
     * @param camera camera
     * @param holder SurfaceHolder
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    public void capture() {
        Camera.Parameters params = mCamera.getParameters();
        params.setPictureFormat(ImageFormat.JPEG);
//        params.setPreviewSize(800, 400);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(params);
        // 使用自动对焦功能
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                }
            }
        });
        mCamera.takePicture(null, null, mPictureCallback);

    }

    /**
     * Camera回调，通过data[]保持图片数据信息
     */
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken() called with: data = [" + data + "], camera = [" + camera + "]");
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
            mIwaerApplication.service.watch.sendCmd(BleCmd.setCameraNotify(0), new BleCallback() {
                @Override
                public void onSuccess(Object o) {

                }

                @Override
                public void onFailure(BleException exception) {

                }
            });
            showToast("保存成功"+pictureFile.getPath().toString());
        }
    };


    private File getOutputMediaFile(){
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(picDir.getPath() + File.separator+"IMG_"+ timeStamp + ".jpg");
    }

    /**
     * 切换前后摄像头
     *
     * @param view view
     */
    public void switchCamera(View view) {
        int cameraCount;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        // 遍历可用摄像头
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (isBackCameraOn) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    releaseCamera();
                    mCamera = Camera.open(i);
                    setStartPreview(mCamera, mSurfaceHolder);
                    isBackCameraOn = false;
                    break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    releaseCamera();
                    mCamera = Camera.open(i);
                    setStartPreview(mCamera, mSurfaceHolder);
                    isBackCameraOn = true;
                    break;
                }
            }
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMessage event) {
        if (event.getWhat() == EventGlobal.ACTION_CAMERA_CAPTURE) {
            showToast("开始拍照");
            capture();
        }else if (event.getWhat() == EventGlobal.ACTION_CAMERA_EXIT){
            showToast("退出拍照");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.checkCameraHardware(this) && (mCamera == null)) {
            mCamera = getCamera();
            if (mSurfaceHolder != null) {
                setStartPreview(mCamera, mSurfaceHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIwaerApplication.service.watch.sendCmd(BleCmd.setCameraViewOnOff(0), new BleCallback() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFailure(BleException exception) {

            }
        });
    }

}
