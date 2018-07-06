package com.per.epx.easytrain.views.activities.search.advanced;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.oushangfeng.pinnedsectionitemdecoration.callback.OnHeaderClickListener;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.HeaderAndItemsAdapter;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.HeaderInfo;
import com.per.epx.easytrain.models.req.DepotTimetableAsk;
import com.per.epx.easytrain.models.req.DepotTimetableReply;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.models.wrapper.SegmentWrapper;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.views.activities.base.BaseActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

public class DepotTimetableDetailActivity extends BaseActivity
        implements DatePickerDialog.OnDateSetListener, Interceptor{
    public static final String KEY_PUT_TIMETABLE_ASK = "KEY_PUT_TIMETABLE_ASK";
    private static final String TAG_SHOW_DATE_DIALOG = "TAG_SHOW_DATE_DIALOG";
    private static final int ASK_KEY = 10000;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RcvListAdapterBase<Object> adapter;
    private DepotTimetableAsk askPkg;
    private View emptyTimetableView;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_detail_depot_timetable;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        mSwipeRefreshLayout = findViewById(R.id.srl_swipe_refresh);
        emptyTimetableView = findViewById(R.id.view_empty_timetable);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.MAGENTA);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing(mSwipeRefreshLayout);
            }
        });

        RecyclerView rcvLines = findViewById(R.id.rcv_depots_of_line);
        rcvLines.setLayoutManager(new LinearLayoutManager(this));
        rcvLines.setAdapter((adapter = new HeaderAndItemsAdapter<>(new RcvAdapterBase.IViewHolderFactory(){

            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                switch (viewType){
                    case HeaderAndItemsAdapter.typeOfTopView:{
                        return new RcvAdapterBase.VHViewParentBase<HeaderInfo>(parent, R.layout.template_header_top_image_label) {
                            @Override
                            protected void onBindData(IHolderApi holderApi, HeaderInfo info, RcvAdapterBase.Payload payload) {
                                holderApi.setText(R.id.tv_title_of_header, info.getTitle());
                                holderApi.setText(R.id.tv_subtitle_of_header, info.getSubtitle());
                                holderApi.setImageSrc(R.id.iv_image_of_header, info.getBackgroundResource());
                            }
                        };
                    }
                    case HeaderAndItemsAdapter.typeOfHeader:{
                        return new RcvAdapterBase.VHViewParentBase<String>(parent, R.layout.template_header_group_label) {
                            @Override
                            protected void onBindData(IHolderApi holderApi, String data, RcvAdapterBase.Payload payload) {
                                holderApi.setText(R.id.tv_group_header, data);
                                holderApi.setVisibility(R.id.iv_modify_in_header, View.VISIBLE);
                                holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                                    @Override
                                    public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                        showModifyDateDialog();
                                    }
                                });
                            }
                        };
                    }
                    default:{
                        return new RcvAdapterBase.VHViewParentBase<SegmentWrapper>(parent, R.layout.template_item_line_master) {
                            @Override
                            protected void onBindData(IHolderApi holderApi, final SegmentWrapper data, RcvAdapterBase.Payload payload) {
                                holderApi.setText(R.id.tv_line_detail_no, data.getLineNo());
                                holderApi.setText(R.id.tv_line_detail_begin_name, data.getBeginName());
                                holderApi.setText(R.id.tv_line_detail_terminal_name, data.getTerminalName());
                                holderApi.setText(R.id.tv_line_detail_begin_drive_time, data.getBeginHourMinuteText());
                                holderApi.setText(R.id.tv_line_detail_terminal_arrive_time, data.getTerminalHourMinuteText());
                                holderApi.setText(R.id.tv_line_detail_time_usage, data.getTimeUsageText());
                                holderApi.setText(R.id.tv_line_detail_cross_size, data.getCrossSize());
                                if(data.raw().getPayment() <= 0){
                                    holderApi.setVisibility(R.id.tv_line_detail_pay, View.GONE);
                                }else{
                                    holderApi.setVisibility(R.id.tv_line_detail_pay, View.VISIBLE);
                                    holderApi.setText(R.id.tv_line_detail_pay, data.getPayment());
                                }
                                if(data.getDayDiffer() > 0){
                                    holderApi.setVisibility(R.id.tv_line_detail_day_plus, View.VISIBLE);
                                    holderApi.setText(R.id.tv_line_detail_day_plus, String.format(Locale.getDefault(), getString(R.string.over_day_format), data.getDayDiffer()));
                                }else{
                                    holderApi.setVisibility(R.id.tv_line_detail_day_plus, View.GONE);
                                }
                                holderApi.setOnClickListener(R.id.pane_root_line_detail, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(DepotTimetableDetailActivity.this, LineDetailActivity.class);
                                        intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_REQUEST, new LineDetailAsk(data.raw().getLineCode(),
                                                data.raw().getLineId(),
                                                DateFormatter.getCurrentYearMonthDayInMs()));
                                        startActivity(intent);
                                    }
                                });
                            }
                        };
                    }
                }
            }
        }, new ArrayList<>())));
        rcvLines.addItemDecoration(new PinnedHeaderItemDecoration.Builder(HeaderAndItemsAdapter.typeOfHeader)
                //.setDividerId(R.drawable.divider)
                .enableDivider(false)
                .setClickIds(R.id.ll_modify_date_root)
                .disableHeaderClick(false)
                .setHeaderClickListener(new OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View view, int id, int position) {
                        showModifyDateDialog();
                    }

                    @Override
                    public void onHeaderLongClick(View view, int id, int position) {}
                    @Override
                    public void onHeaderDoubleClick(View view, int id, int position) {}
                })
                .create());
    }

    @Override
    protected void handIntentAfterView(Intent intent) {
        super.handIntentAfterView(intent);
        super.setTitle(R.string.title_timetable_detail);
        if(intent != null){
            askPkg = CommonHelper.safeCastSerializable(intent, KEY_PUT_TIMETABLE_ASK, DepotTimetableAsk.class);
        }
        if(askPkg == null){
            this.finish();
        }
        manualRefresh();
    }

    private void showModifyDateDialog(){
        if(mSwipeRefreshLayout.isRefreshing()){
            return;
        }
        Calendar now = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 2);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        dpd.setMaxDate(max);
        dpd.show(getFragmentManager(), TAG_SHOW_DATE_DIALOG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(TAG_SHOW_DATE_DIALOG);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(askPkg != null){
            Calendar time = Calendar.getInstance();
            time.set(Calendar.YEAR, year);
            time.set(Calendar.MONTH, monthOfYear);
            time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            time.set(Calendar.HOUR_OF_DAY, 0);
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            askPkg.setTimeOfYearMonthDay(time.getTimeInMillis());
            manualRefresh();
        }
    }

    private void manualRefresh(){
        mSwipeRefreshLayout.setRefreshing(true);
        onRefreshing(mSwipeRefreshLayout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApiRedirect.getApi().register(ASK_KEY, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        ApiRedirect.getApi().unregister(this);
        super.onDestroy();
    }

    private void onRefreshing(final SwipeRefreshLayout swipeRefreshLayout){
        emptyTimetableView.setVisibility(View.GONE);
        ApiRedirect.getApi().findTimetable(ASK_KEY, askPkg, new IAction1<DepotTimetableReply>() {
            @Override
            public void invoke(final DepotTimetableReply data) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.data().clear();
                        try{
                            adapter.data().add(new HeaderInfo(data.getDepotName(),
                                    String.format(getString(R.string.subtitle_latest_updated_format), DateFormatter.formatDefault(new Date(), "HH:mm:ss")),
                                    R.mipmap.header_bg));
                            adapter.data().add(String.format(getString(R.string.header_data_list_with_time_format), DateFormatter.formatYMD(data.getTimeOfYearMonthDay())));
                            if(data.getTimetable() != null){
                                adapter.data().addAll(SegmentWrapper.fromSegments(data.getTimetable(), swipeRefreshLayout.getContext()));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            emptyTimetableView.setVisibility(View.VISIBLE);
                            Toast.makeText(swipeRefreshLayout.getContext(), R.string.tips_recv_data_error, Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                        if(adapter.data().size() > 0){
                            emptyTimetableView.setVisibility(View.GONE);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public void onExceptionOccupied(@NonNull Call call, @NonNull Response response, final @NonNull Exception e) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyTimetableView.setVisibility(View.VISIBLE);
                Toast.makeText(DepotTimetableDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull Call call, final @NonNull IOException e) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyTimetableView.setVisibility(View.VISIBLE);
                Toast.makeText(DepotTimetableDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
