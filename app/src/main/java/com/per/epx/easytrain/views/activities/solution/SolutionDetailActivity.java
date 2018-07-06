package com.per.epx.easytrain.views.activities.solution;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.vipulasri.timelineview.LineType;
import com.github.vipulasri.timelineview.TimelineView;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.history.FavoriteHistoryHelper;
import com.per.epx.easytrain.models.FavoriteItem;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.models.sln.LineRoute;
import com.per.epx.easytrain.models.sln.Slice;
import com.per.epx.easytrain.models.wrapper.SegmentWrapper;
import com.per.epx.easytrain.views.activities.base.BaseActivity;
import com.per.epx.easytrain.views.activities.search.advanced.LineDetailActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class SolutionDetailActivity extends BaseActivity {
    public static final String KEY_PUT_SOLUTION = "aha,something here";
    private LiteDetailRouteAdapter adapter;
    private LineRoute route;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_solution;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setupActionBar(bar);
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_favorite:
                        if(route != null){
                            FavoriteHistoryHelper.getInstance().append(SolutionDetailActivity.this,
                                    new FavoriteItem(route));
                        }
                        Toast.makeText(SolutionDetailActivity.this, "Like", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setTitle(R.string.tb_title_activity_sln_detail);
        RecyclerView rcv = findViewById(R.id.rcv_sln);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter((adapter = new LiteDetailRouteAdapter(new LiteDetailViewHolderFactory(this))));
    }

    @Override
    protected void handIntentAfterView(Intent intent) {
        if(adapter != null && intent != null){
            route = CommonHelper.safeCast(intent.getSerializableExtra(KEY_PUT_SOLUTION), LineRoute.class);
            if(route != null){
                Slice begin = route.getBeginning();
                Slice terminal = route.getTerminal();
                super.setTitle(String.format(Locale.getDefault(), "%s(%s) - %s(%s)",
                        begin.getName(),
                        DateFormatter.formatMD(begin.getDriveTime()),
                        terminal.getName(),
                        DateFormatter.formatMD(terminal.getArrivalTime())));
                List<SegmentWrapper> wrappers = SegmentWrapper.fromContinuableSegments(route.getRunLines(), this);
                if(wrappers.size() <= 3){
                    adapter.rebindFactory(new BigDetailViewHolderFactory(this));
                }
                adapter.data().addAll(wrappers);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private static class LiteDetailRouteAdapter extends RcvListAdapterBase<SegmentWrapper>{

        public LiteDetailRouteAdapter(@NonNull IViewHolderFactory vmFactory) {
            super(vmFactory);
        }

        public void rebindFactory(@NonNull IViewHolderFactory vmFactory){
            this.vmFactory = vmFactory;
        }
    }

    private static class BigDetailViewHolderFactory implements RcvAdapterBase.IViewHolderFactory{
        private final String formatWaitTime;
        private final String formatOverDay;
        private final String nostopText;
        private final WeakReference<Activity> activityRef;

        public BigDetailViewHolderFactory(Activity activity){
            formatWaitTime = activity.getString(R.string.format_wait_time);
            formatOverDay = activity.getString(R.string.over_day_format);
            nostopText = activity.getString(R.string.label_nonstop);
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, final int viewType) {
            return new RcvAdapterBase.VHViewParentBase<SegmentWrapper>(parent, R.layout.template_item_sln_detil_big) {
                @Override
                protected void onBindData(IHolderApi holderApi, final SegmentWrapper data, RcvAdapterBase.Payload payload) {
                    holderApi.setText(R.id.tv_detail_big_route_line_no, data.getLineNo());
                    holderApi.setText(R.id.tv_detail_big_route_pay, data.getPayment());
                    holderApi.setText(R.id.tv_detail_big_route_cross_size, data.getCrossSize());
                    holderApi.setText(R.id.tv_detail_big_route_begin_date_mmdd, data.getBeginMonthDayText());
                    holderApi.setText(R.id.tv_detail_big_route_begin_time_hhmm, data.getBeginHourMinuteText());
                    holderApi.setText(R.id.tv_detail_big_route_begin_time, data.getBeginHourMinuteText());
                    holderApi.setText(R.id.tv_detail_big_route_begin_name, data.getBeginName());
                    holderApi.setText(R.id.tv_detail_big_route_terminal_time, data.getTerminalHourMinuteText());
                    holderApi.setText(R.id.tv_detail_big_route_terminal_name, data.getTerminalName());
                    holderApi.setText(R.id.tv_detail_big_route_time_usage, data.getTimeUsageText());
                    holderApi.setText(R.id.tv_detail_big_route_order, String.valueOf(data.getOrder()));
                    if(payload.itemCount == 1){
                        holderApi.setText(R.id.tv_detail_big_route_wait_for_drive, nostopText);
                    }else if(data.getTimeToLastMs() > 0){
                        holderApi.setText(R.id.tv_detail_big_route_wait_for_drive, String.format(formatWaitTime, data.getTimeToLastText()));
                    }
                    if(data.getDayDiffer() > 0){
                        holderApi.setVisibility(R.id.tv_detail_big_route_day_plus, View.VISIBLE);
                        holderApi.setText(R.id.tv_detail_big_route_day_plus, String.format(Locale.getDefault(),  formatOverDay, data.getDayDiffer()));
                    }else{
                        holderApi.setVisibility(R.id.tv_detail_big_route_day_plus, View.GONE);
                    }
                    holderApi.setOnClickListener(R.id.ll_detail_big_route_detail_root, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), LineDetailActivity.class);
                            intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_BEGIN_ORDER, data.raw().getBeginning().getOrder());
                            intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_TERMINAL_ORDER, data.raw().getTerminal().getOrder());
                            intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_REQUEST, new LineDetailAsk(data.raw().getLineCode(),
                                    data.raw().getLineId(),
                                    data.raw().getBeginning().getDriveTime()));
                            Activity activity = activityRef.get();
                            if(activity != null){
                                activity.startActivity(intent);
                            }
                        }
                    });
                }
            };
        }
    }


    private static class LiteDetailViewHolderFactory implements RcvAdapterBase.IViewHolderFactory{
        private final String formatWaitTime;
        private final String formatOverDay;
        private final WeakReference<Activity> activityRef;

        public LiteDetailViewHolderFactory(Activity activity){
            formatWaitTime = activity.getString(R.string.format_wait_time);
            formatOverDay = activity.getString(R.string.over_day_format);
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, final int viewType) {
            return new RcvAdapterBase.VHViewParentBase<SegmentWrapper>(parent, R.layout.template_item_sln_route) {
                @Override
                protected void onBindData(IHolderApi holderApi, final SegmentWrapper data, RcvAdapterBase.Payload payload) {
                    holderApi.setText(R.id.tv_route_line_no, data.getLineNo());
                    holderApi.setText(R.id.tv_route_pay, data.getPayment());
                    holderApi.setText(R.id.tv_route_cross_size, data.getCrossSize());
                    holderApi.setText(R.id.tv_route_begin_date_mmdd, data.getBeginMonthDayText());
                    holderApi.setText(R.id.tv_route_begin_time_hhmm, data.getBeginHourMinuteText());
                    holderApi.setText(R.id.tv_route_begin_time, data.getBeginHourMinuteText());
                    holderApi.setText(R.id.tv_route_begin_name, data.getBeginName());
                    holderApi.setText(R.id.tv_route_terminal_time, data.getTerminalHourMinuteText());
                    holderApi.setText(R.id.tv_route_terminal_name, data.getTerminalName());
                    holderApi.setText(R.id.tv_route_time_usage, data.getTimeUsageText());
                    holderApi.setText(R.id.tv_route_order, String.valueOf(data.getOrder()));
                    if(data.getTimeToLastMs() > 0){
                        holderApi.setText(R.id.tv_route_wait_for_drive, String.format(formatWaitTime, data.getTimeToLastText()));
                    }
                    if(data.getDayDiffer() > 0){
                        holderApi.setVisibility(R.id.tv_route_day_plus, View.VISIBLE);
                        holderApi.setText(R.id.tv_route_day_plus, String.format(Locale.getDefault(),  formatOverDay, data.getDayDiffer()));
                    }else{
                        holderApi.setVisibility(R.id.tv_route_day_plus, View.GONE);
                    }
                    //Update timeline
                    TimelineView tlv= holderApi.getView(R.id.time_marker);
                    if(payload.itemCount == 1){
                        tlv.initLine(LineType.ONLYONE);
                    }else{
                        if(payload.position == 0){
                            tlv.initLine(LineType.BEGIN);
                        }else if(payload.position == payload.itemCount - 1){
                            tlv.initLine(LineType.END);
                        }else{
                            tlv.initLine(LineType.NORMAL);
                        }
                    }
                    holderApi.setOnClickListener(R.id.ll_route_detail_root, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), LineDetailActivity.class);
                            intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_BEGIN_ORDER, data.raw().getBeginning().getOrder());
                            intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_TERMINAL_ORDER, data.raw().getTerminal().getOrder());
                            intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_REQUEST, new LineDetailAsk(data.raw().getLineCode(),
                                    data.raw().getLineId(),
                                    data.raw().getBeginning().getDriveTime()));
                            Activity activity = activityRef.get();
                            if(activity != null){
                                activity.startActivity(intent);
                            }
                        }
                    });
                }
            };
        }
    }

}
