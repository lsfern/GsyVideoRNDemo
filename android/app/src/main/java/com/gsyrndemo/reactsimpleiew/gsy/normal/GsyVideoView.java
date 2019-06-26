package com.gsyrndemo.reactsimpleiew.gsy.normal;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.android.exoplayer2.SeekParameters;
import com.gsyrndemo.MainActivity;
import com.gsyrndemo.R;
import com.gsyrndemo.reactsimpleiew.gsy.userdefined.LandLayoutVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 自定义view，播放视频
 */
public class GsyVideoView extends LinearLayout {
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
    public static StandardGSYVideoPlayer videoPlayer;

    private static boolean isPlay;
    private static boolean isPause;
    private static int currentPosition;
    public static OrientationUtils orientationUtils;
    private static Handler mSDKHandler = new Handler(Looper.getMainLooper());

    public GsyVideoView(Context context) {
        this(context, null);
    }

    public GsyVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_video, this);
        videoPlayer = view.findViewById(R.id.video_player);
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
        String smallTitle = "";
        String smallTitleColor = "";
        float smallTitleSize = 1;
        boolean isLoop = false;
        if (data.hasKey("isLoop")) {
            isLoop = data.getBoolean("isLoop");
        }
        if (data.hasKey("smallTitle")) {
            smallTitle = data.getString("smallTitle");
        }

        if (data.hasKey("smallTitleSize")) {
            smallTitleSize = data.getInt("smallTitleSize");
        }

        videoPlayer.setUp(url, true, smallTitle);
        videoPlayer.setLooping(isLoop);
        //增加封面
        ImageView imageView = new ImageView(MainActivity.activity);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        if (data.hasKey("smallTitleColor")) {
            videoPlayer.getTitleTextView().setTextColor(Color.parseColor(data.getString("smallTitleColor")));
        }
        videoPlayer.getTitleTextView().setTextSize(smallTitleSize);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.GONE);
        //设置旋转
        orientationUtils = new OrientationUtils(MainActivity.activity, videoPlayer);
        orientationUtils.setEnable(false);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(v -> {
            orientationUtils.resolveByClick();
            videoPlayer.startWindowFullscreen(MainActivity.activity, true, true);
        });
        //是否可以滑动调整
        //设置返回按键功能
//        videoPlayer.getBackButton().setOnClickListener(v -> Toast.makeText(mContext, "ss", Toast.LENGTH_SHORT).show());
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setLooping(isLoop)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setShowPauseCover(true)
                .setRotateWithSystem(false)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(smallTitle)
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

                        if (data.hasKey("bigTitle")) {
                            getCurPlay().getTitleTextView().setText(data.getString("bigTitle"));
                        }
                        if (data.hasKey("bigTitleColor")) {
                            getCurPlay().getTitleTextView().setTextColor(Color.parseColor(data.getString("bigTitleColor")));
                        }
                        if (data.hasKey("bigTitleSize")) {
                            getCurPlay().getTitleTextView().setTextSize((data.getInt("bigTitleSize")));
                        }
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
                .build(videoPlayer);
        videoPlayer.startPlayLogic();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(MainActivity.activity, newConfig, orientationUtils, true, true);
        }
    }

    /**
     * RN点击返回键的回调
     */
    public static void onBackPressed() {
        runOnMainThread(() -> {
            //处理界面抖动的问题
            if (orientationUtils != null) {
                orientationUtils.backToProtVideo();
            }
            //返回是否全屏
            if (GSYVideoManager.backFromWindowFull(MainActivity.activity)) {
//                getCurPlay().setIfCurrentIsFullscreen(false);
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
            GSYVideoManager.releaseAllVideos();
            getCurPlay().setVideoAllCallBack(null);
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
        if (videoPlayer.getFullWindowPlayer() != null) {
            return videoPlayer.getFullWindowPlayer();
        }
        return videoPlayer;
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
