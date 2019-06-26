/**
 * @file 权限管理
 * @date 2019/6/25 10:30
 * @author lsfern
 * @lastModify lsfern 2019/6/25 10:30
 */
import {Alert, NativeModules, Platform} from "react-native";

/**
 * 申请拍照、存储权限
 * @param {number} type -  1：存储；3：拍照
 * TODO 后期如有更多动态权限，需优化
 */
const checkNeedPermission = async (type) => {
    if (Platform.OS === "android") {
        let msg;
        switch (type) {
            case 1: //存储
                msg = await NativeModules.PermissionModule.checkStoragePermission();
                break;
            case 3: //拍照
                msg = await NativeModules.PermissionModule.checkCameraPermisson();
                break;
        }
        // 初始值：-1，允许：0，拒绝：1
        if (msg.level !== -1) { //初始值,忽略
            if (msg.level === 0) {
                return true;
            } else {
                showDialog(type);
                return false;
            }
        }
    }
};

const showDialog = (type) => {
    let message = '';
    switch (type) {
        case 1:
            message = "读写存储权限未开启，请先开启该权限。点击取消将无法继续下载。";
            break;
        case 2:
            break;
        case 3:
            message = "拍照权限未开启，请先开启该权限。点击取消将无法继续使用拍照及相册功能。";
            break;
    }
    Alert.alert(
        "权限申请",
        message,
        [
            {text: "取消 ", onPress: () => alert('未开启所需权限，无法播放')},
            {text: "设置", onPress: () => NativeModules.PermissionModule.goSettings()},
        ],
        {cancelable: false}
    )
};
export default {
    checkNeedPermission
}
