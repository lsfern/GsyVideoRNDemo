package com.gsyrndemo.reactsimpleiew.gsy.normal;

import android.content.Context;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * gsyVideoPlayer播放视频
 */
public class GsyVideoManager extends SimpleViewManager<GsyVideoView>  {
    private Context mContext;
    @Override
    public String getName() {
        return "RCTGsyVideoView";
    }

    @Override
    protected GsyVideoView createViewInstance(ThemedReactContext reactContext) {
        this.mContext = reactContext;
        return new GsyVideoView(reactContext);
    }

    @ReactProp(name = "setPlayVideo")
    public void setPlayVideo(GsyVideoView gsyVideoView, ReadableMap data) {
        gsyVideoView.preparePlay(data);
    }
}
