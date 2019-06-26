package com.gsyrndemo.reactplugin;
import android.app.Activity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.gsyrndemo.utils.PermissionUtil;
import com.yanzhenjie.permission.AndPermission;

/**
 * RN权限管理插件
 */

public class PermissionModule extends ReactContextBaseJavaModule {
    public PermissionModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public String getName() {
        return "PermissionModule";
    }

    /**
     * 申请存储权限
     */
    @ReactMethod
    public void checkStoragePermission(Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            PermissionUtil.getInstance().checkStoragePermission(getCurrentActivity(), promise);
        }
    }

    /**
     * 申请拍照权限
     */
    @ReactMethod
    public void checkCameraPermisson(Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            PermissionUtil.getInstance().checkCameraPermission(getCurrentActivity(), promise);
        }
    }


    /**
     * 跳转到设置界面
     */
    @ReactMethod
    public void goSettings() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            AndPermission.with(activity).runtime().setting().onComeback(() -> {
            }).start();
        }
    }
}
