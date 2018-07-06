package com.per.epx.easytrain.views.activities.solution;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.Selector;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.models.wrapper.RouteWrapper;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class SolutionSelectionActivity extends BaseActivity {
    public static final String KEY_PUT_SOLUTIONS = "aha,KEY_PUT_SOLUTIONS";

    private Selector selector = new Selector();
    private RcvListAdapterBase<RouteWrapper> adapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_select_two_sln;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        super.setTitle(R.string.title_comparison_selection);
        RecyclerView rcv = findViewById(R.id.rcv_slns);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter((adapter = new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory(){
            private int firstSelectedPos = -1;
            private int secondSelectedPos = -1;
            private final @LayoutRes int slnMetaLayout = App.isWideTemplate() ?
                    R.layout.template_item_sln_meta_selectable_big :
                    R.layout.template_item_sln_meta_selectable;
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHViewParentBase<RouteWrapper>(parent, slnMetaLayout) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final RouteWrapper data, RcvAdapterBase.Payload payload) {
                        CheckedTextView checkSln = holderApi.getView(R.id.ctv_check_sln);
                        checkSln.setVisibility(View.VISIBLE);
                        checkSln.setChecked(selector.isSelected(getAdapterPosition()));
                        holderApi.setText(R.id.tv_transfer_lines, data.getTransferText());
                        holderApi.setText(R.id.tv_pay, data.getPayment());
                        holderApi.setText(R.id.tv_transfer_size, data.getCrossSize());
                        holderApi.setText(R.id.tv_begin_time, data.getBeginHourMinuteText());
                        holderApi.setText(R.id.tv_terminal_time, data.getTerminalHourMinuteText());
                        holderApi.setText(R.id.tv_begin_name, data.getBeginName());
                        holderApi.setText(R.id.tv_terminal_name, data.getTerminalName());
                        holderApi.setText(R.id.tv_used_time, data.getTimeUsageText());
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                selector.toggle(adapterPosition);
                                adapter.notifyItemChanged(adapterPosition);
                                if(selector.isSelected(adapterPosition)){
                                    if(firstSelectedPos == -1){
                                        firstSelectedPos = adapterPosition;
                                    }else if(secondSelectedPos == -1){
                                        secondSelectedPos = adapterPosition;
                                    }else{
                                        selector.toggle(firstSelectedPos);
                                        adapter.notifyItemChanged(firstSelectedPos);
                                        firstSelectedPos = secondSelectedPos;
                                        secondSelectedPos = adapterPosition;
                                    }
                                }else{
                                    if(firstSelectedPos == adapterPosition){
                                        firstSelectedPos = secondSelectedPos;
                                        secondSelectedPos = -1;
                                    }else if(secondSelectedPos == adapterPosition){
                                        secondSelectedPos = -1;
                                    }
                                }

                            }
                        });
                        if(data.getDayDiffer() > 0){
                            holderApi.setVisibility(R.id.tv_day_plus, View.VISIBLE);
                            holderApi.setText(R.id.tv_day_plus, String.format(Locale.getDefault(),  getString(R.string.over_day_format), data.getDayDiffer()));
                        }else{
                            holderApi.setVisibility(R.id.tv_day_plus, View.GONE);
                        }
                    }
                };
            }
        })));
        findViewById(R.id.tv_go_comparison).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selector.getSelectedCount() == 2){
                    List<Integer> positions = selector.copySelectedPositionList();
                    RouteWrapper first = adapter.data().get(positions.get(0));
                    RouteWrapper second = adapter.data().get(positions.get(1));
                    Intent intent = new Intent(SolutionSelectionActivity.this, SolutionComparisonActivity.class);
                    intent.putExtra(SolutionComparisonActivity.KEY_PUT_SOLUTION_A, first.raw());
                    intent.putExtra(SolutionComparisonActivity.KEY_PUT_SOLUTION_B, second.raw());
                    startActivity(intent);
                }else{
                    Toast.makeText(v.getContext(), R.string.tip_select_at_least_two, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handIntentAfterView(Intent intent) {
        super.handIntentAfterView(intent);
        if(intent != null){
            Serializable serializable= CommonHelper.safeCastSerializable(intent, KEY_PUT_SOLUTIONS, Serializable.class);
            List<RouteWrapper> routes = CommonHelper.safeCast (serializable, List.class);
            if(routes != null){
                adapter.data().clear();
                adapter.data().addAll(routes);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
