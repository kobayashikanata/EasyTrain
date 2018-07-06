package com.per.epx.easytrain.views.activities.search.advanced;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.history.TextHistoryHelper;
import com.per.epx.easytrain.models.history.TextHistory;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class SearchHistoryFragment extends Fragment {
    private List<TextHistory> histories;
    private RcvListAdapterBase<TextHistory> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_advanced_search_section, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        RecyclerView rcvHistories = view.findViewById(R.id.rcv_result_section);
        rcvHistories.setLayoutManager(new LinearLayoutManager(context));
        rcvHistories.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rcvHistories.setAdapter((adapter = new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHViewParentBase<TextHistory>(parent, R.layout.template_item_history_search) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final TextHistory data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_history_content, data.getText());
                        holderApi.setOnClickListener(R.id.iv_delete_history, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!App.isDebugging()){
                                    TextHistoryHelper.getInstance().remove(getContext(), adapter.data(), data);
                                    updateHistories();
                                    Toast.makeText(getContext(), R.string.tips_deleted, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "DELETE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                EventBus.getDefault().post(new TextHistoryEvent(TextHistoryEvent.SELECTED, data));
                            }
                        });
                    }
                };
            }
        })));
        view.findViewById(R.id.tv_clear_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextHistoryHelper.getInstance().clear(getContext());
                adapter.data().clear();
                adapter.notifyDataSetChanged();
            }
        });
        updateHistories();
    }

    private void updateHistories(){
        //Get history input from share-preference
        histories = TextHistoryHelper.getInstance().get(getContext());
        adapter.data().clear();
        if(histories != null && histories.size() > 0){
            adapter.data().addAll(histories);
        }
        adapter.notifyDataSetChanged();
    }

    public void bindQueryText(String queryText){

    }

    public static class TextHistoryEvent{
        public static final int UPDATE = 0;
        public static final int SELECTED = 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({UPDATE, SELECTED})
        public @interface EventType{}

        public final int eventType;
        public final TextHistory data;

        public TextHistoryEvent(@EventType int eventType){
            this(eventType, null);
        }

        public TextHistoryEvent(@EventType int eventType, TextHistory data){
            this.eventType = eventType;
            this.data = data;
        }
    }
}
