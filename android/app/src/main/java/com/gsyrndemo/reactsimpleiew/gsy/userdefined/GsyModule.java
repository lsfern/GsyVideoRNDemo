package com.gsyrndemo.reactsimpleiew.gsy.userdefined;

import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class GsyModule extends ReactContextBaseJavaModule {
    private Context mContext;

    public GsyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext = reactContext;
    }

    public String getName() {
        return "GsyModule";
    }

    /**
     * 设置全屏
     */
    @ReactMethod
    public void setFullScreen() {
        if (getCurrentActivity() != null) {
            GsyView.setFullScreen();
        }
    }

    /**
     * 点击back键
     */
    @ReactMethod
    public void backAndroid() {
        if (getCurrentActivity() != null) {
            GsyView.onBackPressed();
        }
    }

    /**
     * 截图并保存到系统相册
     * @param {String} folderName - 文件夹名
     * @param {String} imageName - 图片名
     */
    @ReactMethod
    public void shotImage(String folderName,String imageName) {
        if (getCurrentActivity() != null) {
            GsyView.shotImage(folderName,imageName);
        }
    }

    /**
     * 播放
     */
    @ReactMethod
    public void play() {
        if (getCurrentActivity() != null) {
            GsyView.play();
        }
    }

    /**
     * 恢复播放
     */
    @ReactMethod
    public void resume() {
        if (getCurrentActivity() != null) {
            GsyView.resume();
        }
    }

    /**
     * 暂停
     */
    @ReactMethod
    public void pause() {
        if (getCurrentActivity() != null) {
            GsyView.pause();
        }
    }

    /**
     * 停止播放并释放资源
     */
    @ReactMethod
    public void stopAndRelease() {
        if (getCurrentActivity() != null) {
            GsyView.stopAndRelease();
        }
    }
}
