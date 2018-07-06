package com.per.epx.easytrain.views.activities.base;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.per.epx.easytrain.R;

public abstract class BaseActivity0 extends BaseLangSupportActivity {
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());
        //Setup toolbar
        Toolbar toolbar  = getToolBar();
        if(toolbar != null){
            setSupportActionBar(toolbar);
            ActionBar bar = getSupportActionBar();
            if(bar != null){
                bar.setDisplayShowTitleEnabled(false);
                bar.setDisplayHomeAsUpEnabled(true);
                setupActionBar(toolbar);
//                try{
//                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            }
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        beforeSetupViews();
        setupViews(savedInstanceState);
        handIntentAfterView(getIntent());
    }

    protected abstract  @LayoutRes int getContentLayout();

    protected void beforeSetupViews(){

    }

    protected void setupViews(Bundle savedInstanceState){

    }

    protected void setTitleView(@IdRes int id){
        this.titleView = findViewById(id);
    }

    protected void setTitleView(TextView titleView){
        this.titleView = titleView;
    }

    protected void handIntentAfterView(Intent intent){

    }

    protected void setupActionBar(Toolbar bar){

    }

    protected abstract Toolbar getToolBar();

    public void setTitle(CharSequence text){
        if(titleView != null){
            titleView.setText(text);
        }
    }

    public void setTitle(@StringRes int resId){
        if(titleView != null) {
            titleView.setText(resId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
