package com.alibaba.testbundle;

import android.content.Context;
import android.taobao.atlas.runtime.AtlasPreLauncher;
import android.util.Log;

/**
 * Created by chengong.cg on 2017/11/29.
 */

public class PreLaunch implements AtlasPreLauncher {
    private static final String TAG = "PreLaunch";
    @Override
    public void initBeforeAtlas(Context context) {
        Log.i(TAG, "initBeforeAtlas");
    }
}
