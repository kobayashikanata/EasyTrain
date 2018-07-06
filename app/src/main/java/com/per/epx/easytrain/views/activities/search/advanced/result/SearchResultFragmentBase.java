package com.per.epx.easytrain.views.activities.search.advanced.result;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.views.activities.search.advanced.AdvancedSearchActivity;
import com.per.epx.easytrain.views.fragments.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class SearchResultFragmentBase<T extends RecyclerView.Adapter> extends BaseFragment {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TextView tvTest2;
    protected View emptyView;
    protected T adapter;
    private String keyword;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostRefreshEvents(AdvancedSearchActivity.PostRefreshEvent event){
        if(event.type.get() == getEventType()){
            EventBus.getDefault().removeStickyEvent(event);
            onQuery(event.query.get(), event.isSubmit.get());
        }
    }

    protected abstract int getEventType();

    protected abstract void queryImpl(SwipeRefreshLayout mSwipeRefreshLayout, T adapter, String keyword);

    protected abstract T getResultRecyclerViewAdapter();

    public void refreshIfNeed(String keyword){
        onQuery(keyword, false);
    }

    private void onQuery(String keyword, boolean forceRefresh){
        if(keyword == null || keyword.length() == 0)return;
        if(forceRefresh || !keyword.equals(this.keyword)){
            this.keyword = keyword;
            mSwipeRefreshLayout.setRefreshing(true);
            queryImpl(mSwipeRefreshLayout, adapter, keyword);
        }
    }

    @Override
    protected void setupData() {

    }

    @Override
    protected void setupViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvTest2 = view.findViewById(R.id.tv_test2);
        emptyView = view.findViewById(R.id.view_empty_search);
        mSwipeRefreshLayout = view.findViewById(R.id.srl_timetable_depot_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.MAGENTA);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(TextUtils.isEmpty(keyword))return;
                onQuery(keyword, true);
            }
        });
        RecyclerView rcvResultList = view.findViewById(R.id.rcv_timetable_search_depot);
        rcvResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvResultList.setAdapter((adapter = getResultRecyclerViewAdapter()));
    }

    @Override
    protected View createViewFrom(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_search_result_base, container, false);
    }

}
