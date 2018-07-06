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
import com.per.epx.easytrain.models.Depot;
import com.per.epx.easytrain.models.req.DepotTimetableAsk;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.viewmodels.DepotsContactViewModel;
import com.per.epx.easytrain.views.activities.search.advanced.DepotTimetableDetailActivity;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class DepotResultForTimetableFragment extends SearchResultFragmentBase<RcvListAdapterBase<Depot>>
        implements Interceptor {
    private static final int ASK_KEY = 10005;

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
        return 1;
    }

    @Override
    protected void queryImpl(final SwipeRefreshLayout mSwipeRefreshLayout, final RcvListAdapterBase<Depot> adapter, final String keyword) {
        tvTest2.setText(keyword);
        emptyView.setVisibility(View.GONE);
        if(adapter != null){
            adapter.data().clear();
            ApiRedirect.getDepotsContact(ASK_KEY, new IAction1<DepotsContactViewModel>() {
                @Override
                public void invoke(DepotsContactViewModel contact) {
                    final List<Depot> data = contact.findDepotsOfKeyword(keyword);
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.data().addAll(data);
                            adapter.notifyDataSetChanged();
                            if(data.size() == 0){
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
    protected RcvListAdapterBase<Depot> getResultRecyclerViewAdapter() {
        return new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHViewParentBase<Depot>(parent, R.layout.template_simple_list_item_ripple2) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final Depot data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_common_text2, data.getName());
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                Intent intent = new Intent(getActivity(), DepotTimetableDetailActivity.class);
                                intent.putExtra(DepotTimetableDetailActivity.KEY_PUT_TIMETABLE_ASK, new DepotTimetableAsk(
                                        data.getId(),
                                        data.getCode(),
                                        DateFormatter.getCurrentYearMonthDayInMs()
                                ));
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
