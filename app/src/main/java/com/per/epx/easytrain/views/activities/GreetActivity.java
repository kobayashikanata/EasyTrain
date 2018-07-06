package com.per.epx.easytrain.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.SharedPreferencesUtils;
import com.per.epx.easytrain.views.activities.base.BaseLangSupportActivity;

public class GreetActivity extends BaseLangSupportActivity {
    private final Handler mHideHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_greet);
        mHideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean firstTimeOpen = SharedPreferencesUtils.safelyGet(GreetActivity.this,
                        "LOAD", "FIRST_TIME_OPEN", true, Boolean.class);
                Class<?> nextActivityClass;
                if(firstTimeOpen){
                    nextActivityClass = TutorialActivity.class;
                    SharedPreferencesUtils.put(GreetActivity.this,
                            "LOAD", "FIRST_TIME_OPEN", false);
                }else{
                    nextActivityClass = MainActivity.class;
                }
                Intent mIntent = new Intent(GreetActivity.this, nextActivityClass);
                startActivity(mIntent);
                finish();
            }
        }, 1000);
    }

}
