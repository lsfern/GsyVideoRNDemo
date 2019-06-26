package com.gsyrndemo.utils;

import android.app.Activity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

/**
 * 权限管理类
 */

public class PermissionUtil {
    private volatile static PermissionUtil INSTANCE;
    private int level = -1;

    /**
     * 单例
     *
     * @return 实例
     */
    public static PermissionUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (PermissionUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PermissionUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 权限枚举
     */
    public enum permissionErrorEnum {
        /* 允许 */
        ALLOW(0),
        /* 拒绝 */
        DENIAL(1);
        private int result;

        permissionErrorEnum(int result) {
            this.result = result;
        }

        private int getValue() {
            return this.result;
        }
    }

    /**
     * 动态检查存储权限
     */
    public int checkStoragePermission(Activity context, Promise promise) {
        AndPermission.with(context).runtime().permission(Permission.Group.STORAGE)// 为了兼容Android8.O，直接申请存储权限组
                .onGranted(permissions -> {
                    level = permissionErrorEnum.ALLOW.getValue();
                    writeResult(level, promise);
                })
                .onDenied(permissions -> {
                    level = permissionErrorEnum.DENIAL.getValue();
                    writeResult(level, promise);
                }).start();
        return level;
    }

    /**
     * 动态检查拍照权限
     */
    public int checkCameraPermission(Activity context, Promise promise) {
        AndPermission.with(context).runtime().permission(Permission.Group.CAMERA)// 为了兼容Android8.O，直接申请拍照权限组
                .onGranted(permissions -> {
                    level = permissionErrorEnum.ALLOW.getValue();
                    writeResult(level, promise);
                })
                .onDenied(permissions -> {
                    level = permissionErrorEnum.DENIAL.getValue();
                    writeResult(level, promise);
                }).start();

        return level;
    }

    /**
     * 回传数据
     *
     * @param level   权限状态值
     * @param promise 回传接口
     */
    private void writeResult(int level, Promise promise) {
        WritableMap writableMap = new WritableNativeMap();
        writableMap.putInt("level", level);
        promise.resolve(writableMap);
    }

}
