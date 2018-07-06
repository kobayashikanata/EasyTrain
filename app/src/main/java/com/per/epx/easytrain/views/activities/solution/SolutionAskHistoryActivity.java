package com.per.epx.easytrain.views.activities.solution;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.history.AskHistoryHelper;
import com.per.epx.easytrain.models.history.SolutionAskHistory;
import com.per.epx.easytrain.views.activities.base.BaseActivity;
import com.per.epx.easytrain.views.activities.MainActivity;

import java.util.List;

public class SolutionAskHistoryActivity extends BaseActivity {
    private RcvListAdapterBase<SolutionAskHistory> adapter;
    private View emptyView;
    private View clearView;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_history;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        emptyView = findViewById(R.id.view_empty_ask_histories);
        clearView = findViewById(R.id.tv_clear_ask_history);
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!App.isDebugging()){
                    AskHistoryHelper.getInstance().clear(SolutionAskHistoryActivity.this);
                    updateHistories();
                    Toast.makeText(SolutionAskHistoryActivity.this, R.string.tips_clear_ok, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SolutionAskHistoryActivity.this, "CLEAR", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RecyclerView rcvHistories = findViewById(R.id.rcv_histories);
        rcvHistories.setLayoutManager(new LinearLayoutManager(this));
        rcvHistories.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcvHistories.setAdapter((adapter = new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHViewParentBase<SolutionAskHistory>(parent, R.layout.template_item_history_solution_ask) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final SolutionAskHistory data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_sln_history_from, data.getFrom().getName());
                        holderApi.setText(R.id.tv_sln_history_to, data.getTo().getName());
                        holderApi.setOnClickListener(R.id.iv_sln_history_delete, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!App.isDebugging()){
                                    AskHistoryHelper.getInstance().remove(SolutionAskHistoryActivity.this, adapter.data(), data);
                                    updateHistories();
                                    Toast.makeText(SolutionAskHistoryActivity.this, R.string.tips_deleted, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(SolutionAskHistoryActivity.this, "DELETE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                Intent intent = new Intent(SolutionAskHistoryActivity.this, MainActivity.class);
                                intent.putExtra(MainActivity.KEY_PUT_PRE_FILL_FROM, data.getFrom());
                                intent.putExtra(MainActivity.KEY_PUT_PRE_FILL_TO, data.getTo());
                                startActivity(intent);
                                SolutionAskHistoryActivity.this.finish();
                            }
                        });
                    }
                };
            }
        })));
        updateHistories();
    }

    private void updateHistories(){
        List<SolutionAskHistory> askList = AskHistoryHelper.getInstance().get(this);
        adapter.data().clear();
        if(askList != null && askList.size() > 0){
            adapter.data().addAll(askList);
            emptyView.setVisibility(View.GONE);
            clearView.setEnabled(true);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            clearView.setEnabled(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setupActionBar(bar);
        super.setTitle(R.string.tb_title_history);
    }
}
