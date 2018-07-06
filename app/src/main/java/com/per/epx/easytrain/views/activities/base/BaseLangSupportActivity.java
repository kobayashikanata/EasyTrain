package com.per.epx.easytrain.views.activities.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.per.epx.easytrain.App;
import com.per.epx.easytrain.helpers.LanguageUtil;

public abstract class BaseLangSupportActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, App.getLanguage()));
    }

}
