package com.per.epx.easytrain.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.FileUtils;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

import java.util.List;

public class AboutActivity extends BaseActivity {
    private RcvListAdapterBase<ReferenceModel> adapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_about;
    }


    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        ((TextView)findViewById(R.id.tv_version)).setText(App.getVersionName(this));
        RecyclerView rcvRef = findViewById(R.id.rcv_refs);
        rcvRef.setHasFixedSize(true);
        rcvRef.setNestedScrollingEnabled(false);
        rcvRef.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rcvRef.setAdapter((adapter = new RcvListAdapterBase<>(new RcvAdapterBase.IViewHolderFactory() {
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                return new RcvAdapterBase.VHLayoutBase<ReferenceModel>(parent.getContext(), R.layout.template_item_references) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, ReferenceModel data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_ref_name, data.getName());
                        holderApi.setText(R.id.tv_ref_link, data.getLink());
                    }
                };
            }
        })));
        String references = FileUtils.readAssertResource(this, "references.txt");
        if(!TextUtils.isEmpty(references)){
            List<ReferenceModel> refList = new Gson().fromJson(references, new TypeToken<List<ReferenceModel>>(){}.getType());
            adapter.rebind(refList);
        }
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setupActionBar(bar);
        super.setTitle(R.string.tb_title_about);
    }

    public static class ReferenceModel{
        private String name;
        private String link;

        public ReferenceModel() {
        }

        public ReferenceModel(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
