package com.gsyrndemo.reactsimpleiew.gsy.userdefined;

import android.content.Context;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 自定义gsyVideoPlayer展示样式，并可以实时播放rtsp流
 */
public class GsyManager extends SimpleViewManager<GsyView>  {
    private Context mContext;
    private static final int COMMAND_FULL_SCREEN = 1;
    private static final int COMMAND_RESET = 2;
    @Override
    public String getName() {
        return "RCTGsyUserView";
    }

    @Override
    protected GsyView createViewInstance(ThemedReactContext reactContext) {
        this.mContext = reactContext;
        return new GsyView(reactContext);
    }

    @ReactProp(name = "setPlayRtsp")
    public void setPlayRtsp(GsyView gsyView, ReadableMap data) {
        gsyView.preparePlay(data);
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                "onFullScreen", MapBuilder.of("registrationName", "onSpreadClose"));
    }
    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "play", COMMAND_FULL_SCREEN,
                "reset", COMMAND_RESET
        );
    }

    @Override
    public void receiveCommand(@Nonnull GsyView root, int commandId, @Nullable ReadableArray args) {
        switch (commandId){
            case COMMAND_FULL_SCREEN:
                GsyView.setFullScreen();
                break;
        }
    }
}
