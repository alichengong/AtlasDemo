package com.alibaba.localbundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class localBundleMain extends AppCompatActivity {
    private static final String TAG = "localBundleMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.local_bundle_main);
    }
}
