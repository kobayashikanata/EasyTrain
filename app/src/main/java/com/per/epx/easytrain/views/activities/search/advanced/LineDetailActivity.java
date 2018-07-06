package com.per.epx.easytrain.views.activities.search.advanced;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.HeaderAndItemsAdapter;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.DurationFormatter;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.HeaderInfo;
import com.per.epx.easytrain.models.Pass;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.models.req.LineDetailReply;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class LineDetailActivity extends BaseActivity implements Interceptor {
    public static final String KEY_PUT_LINE_DETAIL_RESULT = "KEY_PUT_LINE_DETAIL_RESULT";
    public static final String KEY_PUT_LINE_DETAIL_REQUEST = "KEY_PUT_LINE_DETAIL_REQUEST";
    public static final String KEY_PUT_LINE_DETAIL_BEGIN_ORDER = "KEY_PUT_LINE_DETAIL_BEGIN_ORDER";
    public static final String KEY_PUT_LINE_DETAIL_TERMINAL_ORDER = "KEY_PUT_LINE_DETAIL_TERMINAL_ORDER";
    private static final int ASK_KEY = 10002;
    private RcvListAdapterBase<Object> adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View emptyDetailView;
    private int highlightBeginOrder = 0;
    private int highlightEndOrder = 0;
    private LineDetailAsk askCopy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApiRedirect.getApi().register(ASK_KEY, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiRedirect.getApi().unregister(this);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_detail_line;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        emptyDetailView = findViewById(R.id.view_empty_detail_line);
        mSwipeRefreshLayout = findViewById(R.id.srl_detail_line_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.MAGENTA);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(askCopy == null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{
                    onRefreshing(askCopy);
                }
            }
        });
        RecyclerView rcvDepots = findViewById(R.id.rcv_detail_line);
        rcvDepots.setLayoutManager(new LinearLayoutManager(this));
        final int textDefaultColor = getResources().getColor(R.color.color_text_depot_default);
        final int textHighlightColor = getResources().getColor(R.color.color_text_depot_highlight);
        rcvDepots.setAdapter((adapter = new HeaderAndItemsAdapter<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case HeaderAndItemsAdapter.typeOfTopView: {
                        return new RcvAdapterBase.VHViewParentBase<HeaderInfo>(parent, R.layout.template_header_top_image_label) {
                            @Override
                            protected void onBindData(IHolderApi holderApi, HeaderInfo info, RcvAdapterBase.Payload payload) {
                                holderApi.setText(R.id.tv_title_of_header, info.getTitle());
                                holderApi.setText(R.id.tv_subtitle_of_header, info.getSubtitle());
                                holderApi.setImageSrc(R.id.iv_image_of_header, info.getBackgroundResource());
                            }
                        };
                    }
                    case HeaderAndItemsAdapter.typeOfHeader: {
                        return new RcvAdapterBase.VHViewParentBase<String>(parent, R.layout.template_item_depot_detail) {
                            @Override
                            protected void onBindData(IHolderApi holderApi, String data, RcvAdapterBase.Payload payload) {
                                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_header_bg));
                            }
                        };
                    }
                    default: {
                        return new RcvAdapterBase.VHViewParentBase<Pass>(parent, R.layout.template_item_depot_detail) {
                            @Override
                            protected void onBindData(IHolderApi holderApi, Pass data, RcvAdapterBase.Payload payload) {
                                holderApi.setText(R.id.tv_depot_order, String.valueOf(data.getOrder()));
                                holderApi.setText(R.id.tv_depot_name, data.getName());
                                if(data.getArrivalTime()<=0){
                                    holderApi.setText(R.id.tv_depot_arrive_time, "----");
                                }else{
                                    holderApi.setText(R.id.tv_depot_arrive_time, DateFormatter.formatDefault(data.getArrivalTime(), "HH:mm"));
                                }
                                if(data.getDriveTime() <= 0){
                                    holderApi.setText(R.id.tv_depot_drive_time, "----");
                                }else{
                                    holderApi.setText(R.id.tv_depot_drive_time, DateFormatter.formatDefault(data.getDriveTime(), "HH:mm"));
                                }
                                if(data.getStayTime() <= 0){
                                    holderApi.setText(R.id.tv_depot_stay_time, "----");
                                }else{
                                    holderApi.setText(R.id.tv_depot_stay_time, DurationFormatter.totalMillsToHourMinute(data.getStayTime()));
                                }
                                int color = textDefaultColor;
                                if(data.getOrder() >= highlightBeginOrder && data.getOrder() <= highlightEndOrder){
                                    color = textHighlightColor;
                                }
                                holderApi.setTextColor(R.id.tv_depot_order, color);
                                holderApi.setTextColor(R.id.tv_depot_name, color);
                                holderApi.setTextColor(R.id.tv_depot_arrive_time, color);
                                holderApi.setTextColor(R.id.tv_depot_drive_time, color);
                                holderApi.setTextColor(R.id.tv_depot_stay_time, color);
                            }
                        };
                    }
                }
            }
        })));
    }

    @Override
    protected void handIntentAfterView(Intent intent) {
        super.handIntentAfterView(intent);
        if(intent != null){
            LineDetailReply reply = CommonHelper.safeCastSerializable(intent, KEY_PUT_LINE_DETAIL_RESULT, LineDetailReply.class);
            highlightBeginOrder = intent.getIntExtra(KEY_PUT_LINE_DETAIL_BEGIN_ORDER, 0);
            highlightEndOrder = intent.getIntExtra(KEY_PUT_LINE_DETAIL_TERMINAL_ORDER, 0);
            intent.getIntExtra(KEY_PUT_LINE_DETAIL_BEGIN_ORDER, 0);
            if(reply != null){
                askCopy = new LineDetailAsk(reply.getLineCode(), reply.getLineId(), reply.getTimeOfYearMonthDay());
                applyReply(reply);
            }else{
                LineDetailAsk ask = CommonHelper.safeCastSerializable(intent, KEY_PUT_LINE_DETAIL_REQUEST, LineDetailAsk.class);
                if(ask != null){
                    askCopy = new LineDetailAsk(ask.getLineCode(), ask.getLineId(), ask.getTimeOfYearMonthDay());
                    mSwipeRefreshLayout.setRefreshing(true);
                    onRefreshing(ask);
                }
            }
        }
    }

    private void onRefreshing(@NonNull LineDetailAsk ask){
        emptyDetailView.setVisibility(View.GONE);
        super.setTitle(R.string.title_searching);
        ApiRedirect.getApi().findLine(ASK_KEY, ask, new IAction1<LineDetailReply>() {
            @Override
            public void invoke(final LineDetailReply data) {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        applyReply(data);
                        emptyDetailView.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
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
                emptyDetailView.setVisibility(View.VISIBLE);
                Toast.makeText(LineDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull Call call, final @NonNull IOException e) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyDetailView.setVisibility(View.VISIBLE);
                Toast.makeText(LineDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyReply(@Nullable LineDetailReply reply){
        adapter.data().clear();
        if(highlightBeginOrder < 0){
            highlightBeginOrder = 0;
        }
        if(isFullReply(reply)){
            emptyDetailView.setVisibility(View.GONE);
            if(reply.getPasses() != null){
                if(highlightEndOrder == 0 || highlightEndOrder > reply.getPasses().size()){
                    highlightEndOrder = reply.getPasses().size();
                }
            }
            String title = reply.getName() + "\n" + reply.getBeginning().getName() + "-"+ reply.getTerminal().getName();
            super.setTitle(reply.getName());
            adapter.data().add(new HeaderInfo(title, DateFormatter.formatYMD(reply.getTimeOfYearMonthDay()), R.mipmap.header_bg));
            adapter.data().add("");
            if(reply.getPasses() != null && reply.getPasses().size() > 0){
                adapter.data().addAll(reply.getPasses());
            }
        }else{
            emptyDetailView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private boolean isFullReply(LineDetailReply reply){
        return reply != null && reply.getBeginning() != null
                && reply.getTerminal() != null;
    }
}
