package com.alibaba.remotebundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class RemoteBundleMain extends AppCompatActivity {
    private static final String TAG = "RemoteBundleMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.remote_bundle_main);
    }
}
