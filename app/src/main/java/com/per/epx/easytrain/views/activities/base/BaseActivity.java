package com.per.epx.easytrain.views.activities.base;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.per.epx.easytrain.R;

public abstract class BaseActivity extends BaseLangSupportActivity {
    private TextView tvTitle;
    @SuppressWarnings("FieldCanBeLocal")
    private FrameLayout contentLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        contentLayer = findViewById(R.id.fl_layer);
        tvTitle = findViewById(R.id.tv_common_title);
        //Setup toolbar
        Toolbar toolbar  = (Toolbar)findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayShowTitleEnabled(false);
            bar.setDisplayHomeAsUpEnabled(true);
            setupActionBar(toolbar);
//            try{
//                toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        //Setup content view
        contentLayer.addView(LayoutInflater.from(this).inflate(getContentLayout(), null, false));
        setupViews(savedInstanceState);
        handIntentAfterView(getIntent());
    }

    protected abstract  @LayoutRes int getContentLayout();

    protected void setupViews(Bundle savedInstanceState){

    }

    protected void handIntentAfterView(Intent intent){

    }

    protected void setupActionBar(Toolbar bar){

    }

    public void setTitle(CharSequence text){
        tvTitle.setText(text);
    }

    public void setTitle(@StringRes int resId){
        tvTitle.setText(resId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

