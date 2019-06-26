package com.gsyrndemo.reactsimpleiew.gsy.normal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.gsyrndemo.reactsimpleiew.gsy.userdefined.GsyView;


public class GsyVideoModule extends ReactContextBaseJavaModule {
    private Context mContext;

    public GsyVideoModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext = reactContext;
    }

    public String getName() {
        return "GsyVideoModule";
    }

    /**
     * 点击back键
     */
    @ReactMethod
    public void backAndroid() {
        if (getCurrentActivity() != null) {
            GsyVideoView.onBackPressed();
        }
    }

    /**
     * 恢复播放
     */
    @ReactMethod
    public void resume() {
        if (getCurrentActivity() != null) {
            GsyVideoView.resume();
        }
    }

    /**
     * 暂停
     */
    @ReactMethod
    public void pause() {
        if (getCurrentActivity() != null) {
            GsyVideoView.pause();
        }
    }

    /**
     * 停止播放并释放资源
     */
    @ReactMethod
    public void stopAndRelease() {
        if (getCurrentActivity() != null) {
            GsyVideoView.stopAndRelease();
        }
    }
}
