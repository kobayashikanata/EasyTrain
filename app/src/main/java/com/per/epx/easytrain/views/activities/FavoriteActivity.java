package com.per.epx.easytrain.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.DurationFormatter;
import com.per.epx.easytrain.helpers.history.FavoriteHistoryHelper;
import com.per.epx.easytrain.models.FavoriteItem;
import com.per.epx.easytrain.models.wrapper.RouteWrapper;
import com.per.epx.easytrain.views.activities.base.BaseActivity0;
import com.per.epx.easytrain.views.activities.solution.SolutionDetailActivity;
import com.per.epx.easytrain.views.activities.solution.SolutionSelectionActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Jump to page of solutions master
public class FavoriteActivity extends BaseActivity0 implements View.OnClickListener{
    private RcvListAdapterBase<FavoriteItem> adapter;
    private TextView tvEdit;
    private TextView tvClear;
    private View emptyView;
    private boolean isModifyMode = false;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_favorite;
    }

    @Override
    protected Toolbar getToolBar() {
        return findViewById(R.id.toolbar_favorite);
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        super.setTitleView(R.id.tv_title_favorite);
        super.setTitle(R.string.tb_title_activity_favorite);

        (tvEdit = findViewById(R.id.tv_btn_favorite_edit)).setOnClickListener(this);
        (tvClear = findViewById(R.id.tv_clear_favorite)).setOnClickListener(this);
        findViewById(R.id.tv_btn_favorite_comparison).setOnClickListener(this);
        emptyView = findViewById(R.id.view_empty_favorites);
        tvClear.setVisibility(isModifyMode ? View.VISIBLE : View.GONE);

        RecyclerView rcvHistories = findViewById(R.id.rcv_favorites);
        rcvHistories.setLayoutManager(new LinearLayoutManager(this));
        rcvHistories.setAdapter((adapter = new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHViewParentBase<FavoriteItem>(parent, R.layout.template_item_history_favorite_sln) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final FavoriteItem data, RcvAdapterBase.Payload payload) {
                        RouteWrapper wrapper = new RouteWrapper(data.getRoute(), itemView.getContext());
                        holderApi.setText(R.id.tv_sln_favorite_from, wrapper.getBeginName());
                        holderApi.setText(R.id.tv_sln_favorite_to, wrapper.getTerminalName());
                        holderApi.setText(R.id.tv_sln_favorite_begin_time, wrapper.getBeginHourMinuteText());
                        holderApi.setText(R.id.tv_sln_favorite_terminal_time, wrapper.getTerminalHourMinuteText());
                        holderApi.setText(R.id.tv_sln_favorite_route_time_usage, wrapper.getTimeUsageText());
                        holderApi.setText(R.id.tv_sln_favorite_pay, wrapper.getPayment());
                        holderApi.setText(R.id.tv_sln_favorite_transfer_lines, wrapper.getTransferText());
                        if( wrapper.getDayDiffer() > 0 ){
                            holderApi.setVisibility(R.id.tv_sln_favorite_day_plus, View.VISIBLE);
                            holderApi.setText(R.id.tv_sln_favorite_day_plus, String.format(Locale.getDefault(), getString(R.string.over_day_format), wrapper.getDayDiffer()));
                        } else {
                            holderApi.setVisibility(R.id.tv_sln_favorite_day_plus, View.GONE);
                        }
                        if(!isModifyMode){
                            holderApi.setVisibility(R.id.iv_sln_favorite_delete, View.GONE);
                            holderApi.setOnClickListener(R.id.iv_sln_favorite_delete, null);
                        } else {
                            holderApi.setVisibility(R.id.iv_sln_favorite_delete, View.VISIBLE);
                            holderApi.setOnClickListener(R.id.iv_sln_favorite_delete, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!App.isDebugging()){
                                        FavoriteHistoryHelper.getInstance().remove(FavoriteActivity.this, adapter.data(), data);
                                        updateFavorites();
                                        Toast.makeText(FavoriteActivity.this, R.string.tips_deleted, Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(FavoriteActivity.this, "DELETE", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        holderApi.setOnClickListener(R.id.pane_root_favorite, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FavoriteActivity.this, SolutionDetailActivity.class);
                                intent.putExtra(SolutionDetailActivity.KEY_PUT_SOLUTION, data.getRoute());
                                startActivity(intent);
                            }
                        });
                    }
                };
            }
        })));
        updateFavorites();
    }

    private void updateFavorites(){
        List<FavoriteItem> askList = FavoriteHistoryHelper.getInstance().get(this);
        adapter.data().clear();
        if(askList != null && askList.size() > 0){
            adapter.data().addAll(askList);
            emptyView.setVisibility(View.GONE);
            tvEdit.setEnabled(true);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            tvEdit.setEnabled(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_btn_favorite_comparison:{
                if(adapter.data().size() < 2){
                    Toast.makeText(v.getContext(), R.string.tips_comparison_size, Toast.LENGTH_SHORT).show();
                }else{
                    List<RouteWrapper> wrappers = new ArrayList<>();
                    for(FavoriteItem item : adapter.data()){
                        wrappers.add(new RouteWrapper(item.getRoute(), v.getContext()));
                    }
                    Intent intent = new Intent(v.getContext(), SolutionSelectionActivity.class);
                    intent.putExtra(SolutionSelectionActivity.KEY_PUT_SOLUTIONS, (Serializable) wrappers);
                    startActivity(intent);
                }
            }break;
            case R.id.tv_btn_favorite_edit:{
                isModifyMode = !isModifyMode;
                tvEdit.setText(isModifyMode ? R.string.lable_cancel_edit : R.string.label_edit);
                tvClear.setVisibility(isModifyMode ? View.VISIBLE : View.GONE);
                adapter.notifyDataSetChanged();
            }break;
            case R.id.tv_clear_favorite:{
                if(!App.isDebugging()){
                    FavoriteHistoryHelper.getInstance().clear(FavoriteActivity.this);
                    updateFavorites();
                    Toast.makeText(FavoriteActivity.this, R.string.tips_clear_ok, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(FavoriteActivity.this, "CLEAR", Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }
}
