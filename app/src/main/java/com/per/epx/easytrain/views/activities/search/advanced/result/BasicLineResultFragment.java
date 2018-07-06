package com.per.epx.easytrain.views.activities.search.advanced.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.req.LineBaseReply;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.views.activities.search.advanced.LineDetailActivity;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class BasicLineResultFragment extends SearchResultFragmentBase<RcvListAdapterBase<LineBaseReply>>
        implements Interceptor{
    private static final int ASK_KEY = 10004;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ApiRedirect.getApi().register(ASK_KEY, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiRedirect.getApi().unregister(this);
    }

    @Override
    protected int getEventType() {
        return 0;
    }

    @Override
    protected void queryImpl(final SwipeRefreshLayout mSwipeRefreshLayout,
                             final RcvListAdapterBase<LineBaseReply> adapter,
                             String keyword) {
        tvTest2.setText(keyword);
        emptyView.setVisibility(View.GONE);
        if(adapter != null){
            adapter.data().clear();
            ApiRedirect.getApi().findLinesOfKeyword(ASK_KEY, keyword, new IAction1<List<LineBaseReply>>() {
                @Override
                public void invoke(final List<LineBaseReply> data) {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if(data != null && data.size() > 0){
                                adapter.data().addAll(data);
                                adapter.notifyDataSetChanged();
                                emptyView.setVisibility(View.GONE);
                            }else{
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected RcvListAdapterBase<LineBaseReply> getResultRecyclerViewAdapter() {
        return new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHViewParentBase<LineBaseReply>(parent, R.layout.template_item_simple_line_base) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final LineBaseReply data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_line_code, data.getLineCode());
                        holderApi.setText(R.id.tv_line_name, data.getName());
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                Intent intent = new Intent(getActivity(), LineDetailActivity.class);
                                intent.putExtra(LineDetailActivity.KEY_PUT_LINE_DETAIL_REQUEST,
                                        new LineDetailAsk(data.getLineCode(), data.getLineId(), DateFormatter.getCurrentYearMonthDayInMs()));
                                startActivity(intent);
                            }
                        });
                    }
                };
            }
        });
    }

    @Override
    public void onExceptionOccupied(@NonNull Call call, @NonNull Response response, final @NonNull Exception e) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull Call call, final @NonNull IOException e) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
