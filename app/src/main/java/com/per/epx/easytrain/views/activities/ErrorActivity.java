package com.per.epx.easytrain.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.CrashAssistant;
import com.per.epx.easytrain.helpers.FileUtils;
import com.per.epx.easytrain.helpers.GrantHandler;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ErrorActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_error;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        View nextActionView = findViewById(R.id.tv_next_action);
        View collectingView = findViewById(R.id.tv_collecting_error_tips);
        findViewById(R.id.tv_exit_app).setOnClickListener(this);
        findViewById(R.id.tv_back_home).setOnClickListener(this);
        logErrorInfo();
        collectingView.setVisibility(View.GONE);
        nextActionView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setupActionBar(bar);
        super.setTitle(R.string.title_error_page);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_exit_app:
                exitApp();
                break;
            case R.id.tv_back_home:
                backHome();
                break;
        }
    }

    private void exitApp(){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private void backHome(){
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if(intent == null){
            exitApp();
        }else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void logErrorInfo(){
        String trackTrace = getIntent().getStringExtra(CrashAssistant.EXTRA_STACK_TRACE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        final String exMsg = sdf.format(new Date()) + "\n" +
                trackTrace + "\n" + "\n" + "\n";
        Log.e("ERROR", exMsg);
        GrantHandler.requestStoragePermission(this, new GrantHandler.Callback() {
            @Override
            public void onGrantResults(int[] grantResults) {
                FileUtils.write(Environment.getExternalStorageDirectory().getPath() + "/easytrain/crash.txt", true, exMsg);
            }
        });
    }
}
