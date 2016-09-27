package com.yuanyinguoji.livekit;

import android.content.Context;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.qiniu.pili.droid.streaming.StreamingEnv;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ltjin on 16/9/7.
 */
public class LiveKitPackage implements ReactPackage {

    public LiveKitPackage(Context context){
        StreamingEnv.init(context);
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
                new PlayerViewManager(),
                new LiveViewManager()
        );
    }
}
