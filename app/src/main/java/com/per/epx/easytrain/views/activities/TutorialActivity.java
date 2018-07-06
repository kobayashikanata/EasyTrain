package com.per.epx.easytrain.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.views.activities.base.BaseLangSupportActivity;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class TutorialActivity extends BaseLangSupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_tutorial);
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(new PaperOnboardingPage(getString(R.string.tips_title_search),
                getString(R.string.tips_function_search),
                Color.parseColor("#65B0B4"), R.drawable.ic_tutorial_search, R.drawable.ic_tutorial_search));
        elements.add(new PaperOnboardingPage(getString(R.string.tips_title_favorite),
                getString(R.string.tips_function_favorite),
                Color.parseColor("#B3E5FC"), R.drawable.ic_tutorial_favorite, R.drawable.ic_tutorial_favorite));
        elements.add(new PaperOnboardingPage(getString(R.string.tips_title_stores),
                getString(R.string.tips_function_stores),
                Color.parseColor("#9B90BC"), R.drawable.ic_tutorial_history, R.drawable.ic_tutorial_history));
        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container_tutorial, onBoardingFragment).commit();
        onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                Intent mIntent = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
    }
}
