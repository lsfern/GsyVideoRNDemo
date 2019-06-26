package com.gsyrndemo.reactsimpleiew.gsy.userdefined;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.gsyrndemo.MainActivity;
import com.gsyrndemo.R;
import com.gsyrndemo.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * react-native 调用android view
 * android 自定义view 播放rtsp流
 * create LsFern by 2019/6/21
 */
public class GsyView extends LinearLayout {
    private static Context mContext;
    //正常
    public static final int CURRENT_STATE_NORMAL = 0;
    //准备中
    public static final int CURRENT_STATE_PREPAREING = 1;
    //播放中
    public static final int CURRENT_STATE_PLAYING = 2;
    //开始缓冲
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    //暂停
    public static final int CURRENT_STATE_PAUSE = 5;
    //自动播放结束
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    //错误状态
    public static final int CURRENT_STATE_ERROR = 7;
    public static LandLayoutVideo detailPlayer;

    private static boolean isPlay;
    private static boolean isPause;
    private static int currentPosition;
    public static OrientationUtils orientationUtils;
    private static Handler mSDKHandler = new Handler(Looper.getMainLooper());

    public GsyView(Context context) {
        this(context, null);
    }

    public GsyView(Context context, @Nullable AttributeSet attrs) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_ngsy, this);
        detailPlayer = view.findViewById(R.id.detail_player);
    }

    private static void runOnMainThread(Runnable runnable) {
        mSDKHandler.postDelayed(runnable, 0);
    }

    /**
     * 准备播放
     *
     * @param {Object} data - 播放所需资源
     */
    public static void preparePlay(ReadableMap data) {
        if (!data.hasKey("playUrl")) {
            return;
        }
        String url = data.getString("playUrl");
        String videoTitle = "";
        boolean isLoop = false;

        if (data.hasKey("videoTitle")) {
            videoTitle = data.getString("videoTitle");
        }

        if (data.hasKey("isLoop")) {
            isLoop = data.getBoolean("isLoop");
        }
        //如果视频帧数太高导致卡画面不同步
        List<VideoOptionModel> list = new ArrayList<>();
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 30);
        list.add(videoOptionModel);

        //设置rtsp 缓冲问题
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        list.add(videoOptionModel);

        //打开软解码
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 1);
        list.add(videoOptionModel);

        //打开硬解吗
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        list.add(videoOptionModel);

        //设置dns缓存
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", "1");
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
        list.add(videoOptionModel);

        // 无限读
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240);
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        list.add(videoOptionModel);

        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        list.add(videoOptionModel);

        GSYVideoManager.instance().setOptionModelList(list);
        GSYVideoManager.instance().setTimeOut(4000, true);

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(MainActivity.activity, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        Map<String, String> header = new HashMap<>();
        header.put("ee", "33");
        header.put("allowCrossProtocolRedirects", "true");
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setLooping(isLoop)
                .setRotateViewAuto(false)
                .setRotateWithSystem(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setShowPauseCover(false)
                .setUrl(url)
                .setMapHeadData(header)
                .setCacheWithPlay(false)
                .setVideoTitle(videoTitle)
                .setIsTouchWiget(false)
                .setIsTouchWigetFull(false)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
//                        //设置 seek 的临近帧。
//                        if (detailPlayer.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager) {
//                            ((Exo2PlayerManager) detailPlayer.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
//                            Debuger.printfError("***** setSeekParameter **** ");
//                        }
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        WritableMap params = Arguments.createMap();
                        params.putBoolean("isFullScreen", true);
                        sendEvent((ThemedReactContext) mContext, "isFullScreen", params);
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        WritableMap params = Arguments.createMap();
                        params.putInt("currentState", CURRENT_STATE_AUTO_COMPLETE);
                        sendEvent((ThemedReactContext) mContext, "currentState", params);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                        WritableMap params = Arguments.createMap();
                        params.putBoolean("isFullScreen", false);
                        sendEvent((ThemedReactContext) mContext, "isFullScreen", params);
                    }

                    @Override
                    public void onPlayError(String url, Object... objects) {
                        WritableMap params = Arguments.createMap();
                        params.putInt("currentState", CURRENT_STATE_ERROR);
                        sendEvent((ThemedReactContext) mContext, "currentState", params);
                    }

                })
                .setLockClickListener((view, lock) -> {
                    if (orientationUtils != null) {
                        //配合下方的onConfigurationChanged
                        orientationUtils.setEnable(!lock);
                    }
                })
                .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {
                    WritableMap params = Arguments.createMap();
                    params.putInt("currentBuffer", getCurPlay().getBuffterPoint());
                    params.putInt("currentPosition", getCurPlay().getCurrentPositionWhenPlaying());
                    params.putInt("duration", getCurPlay().getDuration());
                    sendEvent((ThemedReactContext) mContext, "videoProgress", params);
                })
                .build(detailPlayer);

    }

    /**
     * `
     * 播放
     */
    public static void play() {
        runOnMainThread(() -> {
            int currentState = getCurPlay().getCurrentState();
            if (currentState != CURRENT_STATE_PREPAREING) {
                WritableMap params = Arguments.createMap();
                params.putInt("currentState", currentState);
                if (currentState == CURRENT_STATE_PLAYING) {
                    pause();
                    params.putInt("currentState", CURRENT_STATE_PAUSE);
                } else if (currentState == CURRENT_STATE_PAUSE) {
                    resume();
                    params.putInt("currentState", CURRENT_STATE_PLAYING);
                } else if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR) {
                    getCurPlay().startPlayLogic();
                    //开始播放了才能旋转和全屏
                    orientationUtils.setEnable(true);
                    isPlay = true;
                    params.putInt("currentState", CURRENT_STATE_PLAYING);
                }
                sendEvent((ThemedReactContext) mContext, "currentState", params);
            }

        });
    }

    /**
     * 设置全屏
     */
    public static void setFullScreen() {
        runOnMainThread(() -> {
            if (detailPlayer != null) {
                int currentState = getCurPlay().getCurrentState();
                WritableMap params = Arguments.createMap();
                params.putInt("currentState", currentState);
                if (currentState != CURRENT_STATE_PLAYING) {
                    if (currentState == CURRENT_STATE_ERROR) {
                        params.putInt("currentState", CURRENT_STATE_ERROR);
                        sendEvent((ThemedReactContext) mContext, "currentState", params);
                        return;
                    } else if (currentState == CURRENT_STATE_PAUSE) {
                        getCurPlay().onVideoResume(true);
                        getCurPlay().seekTo(currentPosition);
//                        resume();
                        params.putInt("currentState", CURRENT_STATE_PLAYING);
                    } else {
                        getCurPlay().startPlayLogic();
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                        params.putInt("currentState", CURRENT_STATE_PLAYING);
                    }
                }
                sendEvent((ThemedReactContext) mContext, "currentState", params);
                //直接横屏
                orientationUtils.resolveByClick();
//            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(MainActivity.activity, true, true);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(MainActivity.activity, newConfig, orientationUtils, true, true);
        }
    }


    /**
     * RN点击返回键的回调
     */
    public static void onBackPressed() {

        //处理界面抖动的问题
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        //返回是否全屏
        if (GSYVideoManager.backFromWindowFull(MainActivity.activity)) {
            getCurPlay().setIfCurrentIsFullscreen(false);
        }
    }

    public static void shotImage(String folderName, String imageName) {
        //获取截图
        detailPlayer.taskShotPic(bitmap -> {
            if (bitmap != null) {
//                    CommonUtil.saveBitmap(bitmap,folderName,imageName);
                CommonUtil.saveBmp2Gallery(mContext, bitmap, folderName, imageName);
            } else {
                Toast.makeText(mContext, "截图失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void resume() {
        runOnMainThread(() -> {
            getCurPlay().onVideoResume(false);
            isPause = false;
        });
    }

    public static void pause() {
        runOnMainThread(() -> {
            getCurPlay().onVideoPause();
            currentPosition = getCurPlay().getCurrentPositionWhenPlaying();
            isPause = true;
        });

    }

    /**
     * 停止播放并释放资源
     */
    public static void stopAndRelease() {
        runOnMainThread(() -> {
            if (isPlay) {
                getCurPlay().release();
            }
            //GSYPreViewManager.instance().releaseMediaPlayer();
            if (orientationUtils != null)
                orientationUtils.releaseListener();
        });
    }

    /**
     * 获取当前播放器
     *
     * @return
     */
    private static GSYVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }


    @Override
    public void requestLayout() {
        super.requestLayout();
        reLayout();
    }

    public void reLayout() {
        if (getWidth() > 0 && getHeight() > 0) {
            int w = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
            int h = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY);
            measure(w, h);
            layout(getPaddingLeft() + getLeft(), getPaddingTop() + getTop(), getWidth() + getPaddingLeft() + getLeft(), getHeight() + getPaddingTop() + getTop());
        }
    }

    public static void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

}
