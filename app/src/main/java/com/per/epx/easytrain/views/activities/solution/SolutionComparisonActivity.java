package com.per.epx.easytrain.views.activities.solution;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.adapters.base.ViewHolderHelper;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.models.sln.LineRoute;
import com.per.epx.easytrain.models.wrapper.RouteWrapper;
import com.per.epx.easytrain.models.wrapper.SegmentWrapper;
import com.per.epx.easytrain.views.activities.base.BaseActivity0;

import java.util.Locale;

public class SolutionComparisonActivity extends BaseActivity0 {
    public static final String KEY_PUT_SOLUTION_A = "aha,KEY_PUT_SOLUTION_A";
    public static final String KEY_PUT_SOLUTION_B = "oops,KEY_PUT_SOLUTION_B";

    private RcvListAdapterBase<SegmentWrapper> adapterA;
    private RcvListAdapterBase<SegmentWrapper> adapterB;
    private BindScrollListener bindRight;
    private BindScrollListener bindLeft;
    private RecyclerView rcvRight;
    private RecyclerView rcvLeft;
    private TextView tvRight;
    private TextView tvLeft;
    private View leftOverview;
    private View rightOverview;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_sln_comparison;
    }

    @Override
    protected Toolbar getToolBar() {
        return findViewById(R.id.toolbar_comparison);
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        super.setTitleView(R.id.tv_title_comparison);
        final ViewGroup ovView = findViewById(R.id.pane_overview);
        final View tvExpand = findViewById(R.id.tv_expand);
        final View ivCollapsed = findViewById(R.id.iv_collapsed);
        tvExpand.setVisibility(View.GONE);
        tvExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ovView.setVisibility(View.VISIBLE);
                ivCollapsed.setVisibility(View.VISIBLE);
                tvExpand.setVisibility(View.GONE);
            }
        });
        ivCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ovView.setVisibility(View.GONE);
                ivCollapsed.setVisibility(View.GONE);
                tvExpand.setVisibility(View.VISIBLE);
            }
        });
        tvLeft = findViewById(R.id.tv_left_from_to);
        tvRight = findViewById(R.id.tv_right_from_to);
        rcvLeft = findViewById(R.id.rcv_left_sln);
        rcvRight = findViewById(R.id.rcv_right_sln);
        leftOverview = findViewById(R.id.ov_left);
        rightOverview = findViewById(R.id.ov_right);
        rcvLeft.setLayoutManager(new LinearLayoutManager(this));
        rcvRight.setLayoutManager(new LinearLayoutManager(this));
        rcvLeft.setAdapter((adapterA = new SolutionCompareAdapter(this)));
        rcvRight.setAdapter((adapterB = new SolutionCompareAdapter(this)));
        rcvLeft.addOnScrollListener((bindRight = new BindScrollListener(rcvRight)));
        rcvRight.addOnScrollListener((bindLeft = new BindScrollListener(rcvLeft)));
        CheckBox stickyCheck = findViewById(R.id.cb_check_sticky);
        stickyCheck.setChecked(true);
        stickyCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rcvLeft.removeOnScrollListener(bindRight);
                rcvRight.removeOnScrollListener(bindLeft);
                if(isChecked){
                    rcvLeft.addOnScrollListener(bindRight);
                    rcvRight.addOnScrollListener(bindLeft);
                }
            }
        });
    }

    @Override
    protected void handIntentAfterView(Intent intent) {
        if(intent != null) {
            LineRoute routeA = CommonHelper.safeCast(intent.getSerializableExtra(KEY_PUT_SOLUTION_A), LineRoute.class);
            LineRoute routeB = CommonHelper.safeCast(intent.getSerializableExtra(KEY_PUT_SOLUTION_B), LineRoute.class);
            if(routeA != null){
                tvLeft.setText(String.format(getString(R.string.format_from_to), routeA.getBeginning().getName(), routeA.getTerminal().getName()));
                adapterA.data().addAll(SegmentWrapper.fromContinuableSegments(routeA.getRunLines(), this));
                adapterA.notifyDataSetChanged();
                setupOverview(routeA, leftOverview);
            }
            if(routeB != null){
                tvRight.setText(String.format(getString(R.string.format_from_to), routeB.getBeginning().getName(), routeB.getTerminal().getName()));
                adapterB.data().addAll(SegmentWrapper.fromContinuableSegments(routeB.getRunLines(), this));
                adapterB.notifyDataSetChanged();
                setupOverview(routeB, rightOverview);
            }
        }
    }

    private void setupOverview(LineRoute route, View ovTemplate){
        RouteWrapper data = new RouteWrapper(route, ovTemplate.getContext());
        ViewHolderHelper holderApi = new ViewHolderHelper(ovTemplate);
        holderApi.setText(R.id.tv_ov_payment, data.getPayment());
        holderApi.setText(R.id.tv_ov_route_begin_time, data.getBeginHourMinuteText());
        holderApi.setText(R.id.tv_ov_route_terminal_time, data.getTerminalHourMinuteText());
        holderApi.setText(R.id.tv_ov_route_time_usage, data.getTimeUsageText());
        if(data.getDayDiffer() > 0){
            holderApi.setVisibility(R.id.tv_ov_route_day_plus, View.VISIBLE);
            holderApi.setText(R.id.tv_ov_route_day_plus, String.format(Locale.getDefault(),  getString(R.string.over_day_format), data.getDayDiffer()));
        }else{
            holderApi.setVisibility(R.id.tv_ov_route_day_plus, View.GONE);
        }
    }

    private static class BindScrollListener extends RecyclerView.OnScrollListener{
        private RecyclerView bindTo;
        public BindScrollListener(RecyclerView bindTo){
            this.bindTo = bindTo;
        }

        public void detach(){
            this.bindTo = null;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(bindTo != null){
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    // note: scrollBy() not trigger OnScrollListener
                    // This is a known issue. It is caused by the fact that RecyclerView does not know how LayoutManager will handle the scroll or if it will handle it at all.
                    bindTo.scrollBy(dx, dy);
                }
            }
        }
    }

    private static class SolutionCompareAdapter extends RcvListAdapterBase<SegmentWrapper>{
        public SolutionCompareAdapter(Context context) {
            super(new CompareItemViewHolderFactory(context));
        }
    }

    private static class CompareItemViewHolderFactory implements RcvAdapterBase.IViewHolderFactory{
        private final String formatWaitTime;
        private final String formatOverDay;

        public CompareItemViewHolderFactory(Context context) {
            this.formatWaitTime = context.getString(R.string.format_wait_time);
            this.formatOverDay = context.getString(R.string.over_day_format);
        }

        @Override
        public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, final int viewType) {
            return new RcvAdapterBase.VHViewParentBase<SegmentWrapper>(parent, R.layout.template_item_sln_comparison) {
                @Override
                protected void onBindData(IHolderApi holderApi, final SegmentWrapper data, RcvAdapterBase.Payload payload) {
                    holderApi.setText(R.id.tv_compare_route_line_no, data.getLineNo());
                    holderApi.setText(R.id.tv_compare_route_pay, data.getPayment());
                    holderApi.setText(R.id.tv_compare_route_cross_size, data.getCrossSize());
                    holderApi.setText(R.id.tv_compare_route_begin_date_mmdd, data.getBeginMonthDayText());
                    holderApi.setText(R.id.tv_compare_route_begin_time_hhmm, data.getBeginHourMinuteText());
                    holderApi.setText(R.id.tv_compare_route_begin_time, data.getBeginHourMinuteText());
                    holderApi.setText(R.id.tv_compare_route_begin_name, data.getBeginName());
                    holderApi.setText(R.id.tv_compare_route_terminal_time, data.getTerminalHourMinuteText());
                    holderApi.setText(R.id.tv_compare_route_terminal_name, data.getTerminalName());
                    holderApi.setText(R.id.tv_compare_route_time_usage, data.getTimeUsageText());
                    holderApi.setText(R.id.tv_compare_route_order, String.valueOf(data.getOrder()));
                    if(data.getTimeToLastMs() > 0){
                        holderApi.setText(R.id.tv_compare_route_wait_for_drive, String.format(formatWaitTime, data.getTimeToLastText()));
                    }
                    TextView textView = holderApi.getView(R.id.tv_compare_route_day_plus);
                    if(data.getDayDiffer() > 0){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(String.format(Locale.getDefault(), formatOverDay, data.getDayDiffer()));
                    }else{
                        textView.setVisibility(View.GONE);
                    }
                }
            };
        }
    }
}
