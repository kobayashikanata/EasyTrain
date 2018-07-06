package com.per.epx.easytrain.views.activities;

import android.support.v7.widget.Toolbar;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_login;
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setTitle(R.string.tb_title_activity_login);
    }
}
