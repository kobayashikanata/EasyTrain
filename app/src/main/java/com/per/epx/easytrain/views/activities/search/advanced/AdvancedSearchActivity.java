package com.per.epx.easytrain.views.activities.search.advanced;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.ActionHelper;
import com.per.epx.easytrain.helpers.ViewFragmentHelper;
import com.per.epx.easytrain.helpers.history.TextHistoryHelper;
import com.per.epx.easytrain.interfaces.IAction2;
import com.per.epx.easytrain.interfaces.IQueryable;
import com.per.epx.easytrain.models.history.TextHistory;
import com.per.epx.easytrain.models.getset.Getter0;
import com.per.epx.easytrain.models.getset.IGetter0;
import com.per.epx.easytrain.views.activities.base.BaseLangSupportActivity;
import com.per.epx.easytrain.views.activities.search.advanced.result.BasicLineResultFragment;
import com.per.epx.easytrain.views.activities.search.advanced.result.DepotResultForTimetableFragment;
import com.per.epx.easytrain.views.fragments.base.BaseFragment;
import com.per.epx.easytrain.views.fragments.base.BaseFragmentFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AdvancedSearchActivity extends BaseLangSupportActivity {
    private TabLayout typeTabs;
    private AppCompatSpinner spinner;
    private SearchController searchViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_search_adv);
        EventBus.getDefault().register(this);
        //Setup toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_search));
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayShowTitleEnabled(false);
            bar.setHomeButtonEnabled(false);
            bar.setDisplayHomeAsUpEnabled(false);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        //Setup content view
        setupViews(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    protected void setupViews(Bundle savedInstanceState) {
        FragmentManager supportMgr = getSupportFragmentManager();
        //Setup history fragment
        Fragment historyFrag = supportMgr.findFragmentById(R.id.history_fragment);
                //.findFragmentByTag(tag), way one
        if(historyFrag == null){
            historyFrag = new SearchHistoryFragment();
            supportMgr.beginTransaction().add(R.id.history_fragment, historyFrag)
                    .commit();//.add(fragment, tag), way one
        }
        //supportMgr.beginTransaction().hide(historyFrag).commit();
        SearchView sv = findViewById(R.id.sv_line);
        //Connect to view-controller
        searchViewController = new SearchController(supportMgr, historyFrag, sv, new IAction2<String, Integer>() {
            @Override
            public void invoke(String queryText, Integer queryType) {
                selectTabAt(queryType);
                EventBus.getDefault().postSticky(new PostRefreshEvent(queryType, queryText));
            }
        });
        //Setup other views
        findViewById(R.id.iv_search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvancedSearchActivity.this.finish();
            }
        });
        //Setup tabs and its ViewPaper
        final TypeFragmentPagerAdapter typeAdapter = new TypeFragmentPagerAdapter(supportMgr, this);
        ViewPager typeResultPaper = findViewById(R.id.vp_type_result);
        typeResultPaper.setAdapter(typeAdapter);
        typeTabs = findViewById(R.id.tabs_type);
        typeTabs.setupWithViewPager(typeResultPaper);
        typeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selected = tab.getPosition();
                spinner.setSelection(selected);
                searchViewController.mSearchView.setQueryHint(typeAdapter.getInputTipsAt(selected));
                searchViewController.setQueryType(selected);
                EventBus.getDefault().postSticky(new PostRefreshEvent(selected,
                        searchViewController.getNewQueryText(), false));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        spinner = findViewById(R.id.spinner_types);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectTabAt(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        sv.setFocusable(true);
        sv.requestFocusFromTouch();
    }


    private void selectTabAt(int position){
        TabLayout.Tab tab = typeTabs.getTabAt(position);
        if(tab != null){
            tab.select();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostRefreshEvents(SearchHistoryFragment.TextHistoryEvent event){
        if(event.eventType == SearchHistoryFragment.TextHistoryEvent.SELECTED){
            searchViewController.mSearchView.setQuery(event.data.getText(), true);
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class SearchController implements SearchView.OnQueryTextListener, View.OnFocusChangeListener{
        private final SearchView mSearchView;
        private final ViewFragmentHelper fragHelper;
        private int queryType;
        private IAction2<String, Integer> onQuery;
        private String newQueryText;

        public SearchController(FragmentManager fragmentManager, Fragment historyFrag, SearchView mSearchView, IAction2<String, Integer> onQuery){
            this.fragHelper = new ViewFragmentHelper(fragmentManager, historyFrag);
            this.mSearchView = mSearchView;
            this.mSearchView.setSubmitButtonEnabled(true);
            this.mSearchView.setOnQueryTextListener(this);
            this.mSearchView.setOnQueryTextFocusChangeListener(this);
            this.onQuery = onQuery;
        }

        public int getQueryType() {
            return queryType;
        }

        public void setQueryType(int queryType) {
            this.queryType = queryType;
        }

        public String getNewQueryText() {
            return newQueryText;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            this.newQueryText = query;
            fragHelper.hide();
            Context context = mSearchView.getContext();
            if(App.isDebugging()){
                Toast.makeText(context, "Your input is " + query, Toast.LENGTH_SHORT).show();
            }
            TextHistoryHelper.getInstance().append(context, new TextHistory(query, 1, 0));
            mSearchView.clearFocus();
            ActionHelper.safelyInvoke(onQuery, query, getQueryType());
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            this.newQueryText = newText;
            //fragHelper.setVisible(newText.trim().length() > 0);
            fragHelper.show();
            Fragment fragment = fragHelper.getFragment();
            if(fragment != null && fragment instanceof IQueryable){
                IQueryable queryable = (IQueryable)fragment;
                queryable.bindQueryText(newText);
            }
            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            fragHelper.setVisible(hasFocus);
        }
    }

    public static class PostRefreshEvent{
        public final IGetter0<String> query;
        public final IGetter0<Integer> type;
        public final IGetter0<Boolean> isSubmit;
        public PostRefreshEvent(int type, String query){
            this(type, query, true);
        }

        public PostRefreshEvent(int type, String query, boolean isSubmit){
            this.type = new Getter0<>(type);
            this.query = new Getter0<>(query);
            this.isSubmit = new Getter0<>(isSubmit);
        }
    }

    private static class TypeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final FragmentFactory factory = new FragmentFactory();
        private final String[] titles;
        private final String[] inputTips;

        public TypeFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            titles = context.getResources().getStringArray(R.array.search_types);
            inputTips = context.getResources().getStringArray(R.array.search_type_tips);
        }

        public String getInputTipsAt(int position) {
            return inputTips[position];
        }



        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return factory.findFragment(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        private static class FragmentFactory extends BaseFragmentFactory {
            @Override
            protected BaseFragment createNewFragment(int position) {
                switch (position){
                    case 0:return new BasicLineResultFragment();
                    case 1:return new DepotResultForTimetableFragment();
                    default:return null;
                }
            }
        }
    }
}
