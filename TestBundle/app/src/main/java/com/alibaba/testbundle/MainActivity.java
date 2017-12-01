package com.alibaba.testbundle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String LocalBundleClassName =
            "com.alibaba.localbundle.localBundleMain";
    private static final String RemoteBundleClassName =
            "com.alibaba.remotebundle.RemoteBundleMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button localBtn = (Button) findViewById(R.id.local_btn);
        localBtn.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "click localBtn");
                Intent intent = new Intent();
                intent.setClassName(view.getContext(), LocalBundleClassName);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.i(TAG, "intent mismatch");
                }
            }
        }));

        Button remoteBtn = (Button) findViewById(R.id.remote_btn);
        remoteBtn.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "click remoteBtn");
                Intent intent = new Intent();
                intent.setClassName(view.getContext(), RemoteBundleClassName);
                startActivity(intent);
            }
        }));

        Button updateBtn = (Button) findViewById(R.id.update_btn);
        updateBtn.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "click updateBtn");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Log.i(TAG, "beg update patch");
                        Updater.update(getBaseContext());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Log.i(TAG, "update patch finish");
                        // android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }.execute();
            }
        }));

        Button hookBtn = (Button) findViewById(R.id.hook_btn);
        hookBtn.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "click hookBtn");
                hookAMS();
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hookAMS() {
        Log.i(TAG, "hookAMS");

        try {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object rawIActivityManager = mInstanceField.get(gDefault);

            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { iActivityManagerInterface }, new HookHandler(rawIActivityManager));
            mInstanceField.set(gDefault, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
