package com.alibaba.testbundle;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.taobao.atlas.bundleInfo.AtlasBundleInfoManager;
import android.taobao.atlas.framework.Atlas;
import android.taobao.atlas.runtime.ActivityTaskMgr;
import android.taobao.atlas.runtime.ClassNotFoundInterceptorCallback;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.osgi.framework.BundleException;

import java.io.File;

/**
 * Created by chengong.cg on 2017/11/20.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tag", "=========AppApplication");

        Atlas.getInstance().setClassNotFoundInterceptorCallback(new ClassNotFoundInterceptorCallback() {

            @Override
            public Intent returnIntent(Intent intent) {
                final String className = intent.getComponent().getClassName();
                final String bundleName = AtlasBundleInfoManager.instance().getBundleForComponet(className);

                if (!TextUtils.isEmpty(bundleName) && !AtlasBundleInfoManager.instance().isInternalBundle(bundleName)) {
                    Activity topActivity = ActivityTaskMgr.getInstance().peekTopActivity();
                    // storage/emulated/0/Android/data/com.alibaba.testbundle/cache
                    File remoteBundleFile = new File(topActivity.getExternalCacheDir(), "lib" + bundleName.replace(".", "_") + ".so");

                    String path = "";
                    if (remoteBundleFile.exists()) {
                        path = remoteBundleFile.getAbsolutePath();
                    } else {
                        String destPath = topActivity.getExternalCacheDir() + "/" + "lib" + bundleName.replace(".", "_") + ".so";
                        Toast.makeText(topActivity, "Remote bundle Miss", Toast.LENGTH_LONG).show();
                        return intent;
                    }

                    PackageInfo info = topActivity.getPackageManager().getPackageArchiveInfo(path, 0);
                    try {
                        Atlas.getInstance().installBundle(info.packageName, new File(path));
                    } catch (BundleException e) {
                        Toast.makeText(topActivity, "install remote bundle failed，" + e.getMessage() , Toast.LENGTH_LONG).show();

                        e.printStackTrace();
                    }

                    topActivity.startActivities(new Intent[]{intent});
                }
                return intent;
            }
        });

    }

}
