package com.per.epx.easytrain.views.activities.search.depot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.SpanGridAdapter;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.SmoothHelper;
import com.per.epx.easytrain.interfaces.DepotSelectedListener;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.Depot;
import com.per.epx.easytrain.models.getset.GetSetter0;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.viewmodels.DepotsContactViewModel;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

@SuppressWarnings("FieldCanBeLocal")
public class DepotSearchActivity extends BaseActivity implements DepotSelectedListener, Interceptor{
    public static final String KEY_GET_SELECTED_PLACE = "SELECTED_PLACE";
    private static final int ASK_KEY = 10001;

    private DepotsContactViewModel depotsContactVm;
    private int callbackCode = -1;
    private DepotSearchFragment searchFrag;
    private SearchView stopSearchView;
    private View emptyView;
    private FrameLayout progUpdateDepot;
    private Handler postHandler = null;

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

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_search_depot;
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setTitle(R.string.tb_title_activity_search_stop);
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        postHandler = new Handler();
        emptyView = findViewById(R.id.view_empty_depot);
        searchFrag = (DepotSearchFragment) getSupportFragmentManager()
                // .findFragmentByTag(tag), way one
                .findFragmentById(R.id.search_fragment);
        if(searchFrag == null){
            searchFrag = new DepotSearchFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    //.add(fragment, tag), way one
                    .add(R.id.search_fragment, searchFrag)
                    .commit();
        }
        stopSearchView = (SearchView) findViewById(R.id.sv_stop);
        progUpdateDepot = (FrameLayout) findViewById(R.id.progress_update_depot);

        connectSearchFragment(stopSearchView, searchFrag);

        RecyclerView rcvIndex  = findViewById(R.id.rv_test);
        RecyclerView.ItemAnimator itemAnimator = rcvIndex.getItemAnimator();
        if(itemAnimator instanceof SimpleItemAnimator){
            ((SimpleItemAnimator)itemAnimator).setSupportsChangeAnimations(false);
        }
        rcvIndex.setLayoutManager(new GridLayoutManager(this, 6));
        rcvIndex.addItemDecoration(new SpanGridAdapter.ItemDecoration(6, CommonHelper.dpToPx(getResources(), 13)));
        setupAdapterBeta(rcvIndex);
    }

    public boolean setupAdapterBeta(final RecyclerView indexRcv){
        final GetSetter0<SpanGridAdapter<DepotVHFactory.SpanItemBase>> adapterTemp = new GetSetter0<>();
        adapterTemp.set(new SpanGridAdapter<>(new DepotVHFactory(new IAction1<DepotVHFactory.PlaceItem>() {
            @Override
            public void invoke(DepotVHFactory.PlaceItem place) {
                Log.i("place-clicked", place.label);
                onDepotSelected(place.entity);
            }
        }, new IAction1<DepotVHFactory.IndexItem>() {
            @Override
            public void invoke(DepotVHFactory.IndexItem index) {
                if(adapterTemp.get() != null && depotsContactVm != null){
                    //Find items
                    List<Depot> resultList = depotsContactVm.findDepotsOfIndex(index.label);
                    //Convert items
                    final List<DepotVHFactory.NormalItem> addItems = new ArrayList<>(resultList.size());
                    for(Depot entity : resultList){
                        addItems.add(new DepotVHFactory.NormalItem(entity, entity.getName()));
                    }
                    //Add to adapter
                    final int fromPos = adapterTemp.get().data().size();
                    //Remove old data
                    adapterTemp.get().removeTypeOf(DepotVHFactory.SpanItemBase.TYPE_NORMAL);
                    adapterTemp.get().data().addAll(addItems);
                    adapterTemp.get().notifyItemRangeInserted(fromPos, addItems.size());
                    //Get the first item of type which is current type
                    RecyclerView.Adapter adapter = adapterTemp.get();
                    int firstOfType = 0, typeCount = 0;
                    for(int m = 0; m < adapter.getItemCount(); m++){
                        if(adapter.getItemViewType(m) == DepotVHFactory.SpanItemBase.TYPE_INDEX){
                            typeCount++;
                            if(typeCount == 1){
                                firstOfType = m;
                            }
                        }
                    }
                    //First visible item
                    int firstVisiblePos = indexRcv.getChildLayoutPosition(indexRcv.getChildAt(0));
                    //Last visible item
                    int lastVisiblePos = indexRcv.getChildLayoutPosition(indexRcv.getChildAt(indexRcv.getChildCount() - 1));
                    SmoothHelper.smoothMoveToPosition(indexRcv, firstOfType, firstVisiblePos, lastVisiblePos);
                }
            }
        }), new ArrayList<DepotVHFactory.SpanItemBase>()));
        indexRcv.setAdapter(adapterTemp.get());

        final List<DepotVHFactory.SpanItemBase> items = new ArrayList<>();
        final Runnable callback = new Runnable() {
            @Override
            public void run() {
                adapterTemp.get().data().clear();
                adapterTemp.get().data().addAll(items);
                adapterTemp.get().notifyDataSetChanged();
                if(items.size() == 0){
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
                progUpdateDepot.setVisibility(View.GONE);
            }
        };
        ApiRedirect.getDepotsContact(ASK_KEY, new IAction1<DepotsContactViewModel>() {
            @Override
            public void invoke(DepotsContactViewModel contact) {
                depotsContactVm = contact;
                //Add most items
                List<Depot> mostDepots = depotsContactVm.getMostDepots(DepotSearchActivity.this);

                if(mostDepots != null && mostDepots.size() > 0){
                    items.add(new DepotVHFactory.HeaderItem(getString(R.string.header_depot_most)));
                    for(Depot most : mostDepots){
                        items.add(new DepotVHFactory.MostItem(most, most.getName()));
                    }
                }
                //Add hot items
                List<Depot> hotDepots = depotsContactVm.getHotDepots();
                if(hotDepots != null && hotDepots.size() > 0){
                    items.add(new DepotVHFactory.HeaderItem(getString(R.string.header_depot_hot)));
                    for(Depot hot : depotsContactVm.getHotDepots())
                        items.add(new DepotVHFactory.HotItem(hot, hot.getName()));
                }
                //Add index items
                items.add(new DepotVHFactory.HeaderItem(getString(R.string.header_depot_all)));
                for(String index : depotsContactVm.getIndexes())
                    items.add(new DepotVHFactory.IndexItem(index));
                postHandler.post(callback);
            }
        });

        return true;
    }

    //Connect resultFragment to searchView in order to show or hide the search result.
    private void connectSearchFragment(final SearchView searchView, final DepotSearchFragment frag) {
        frag.setPlaceSelectedListener(this);
        getSupportFragmentManager().beginTransaction().hide(frag).commit();
        //Setup listener for searchView to show or hide the resultFragment in need.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 0) {
                    if (frag.isHidden()) {
                        getSupportFragmentManager().beginTransaction().show(frag).commit();
                    }
                    frag.setFinding(true);
                    if(depotsContactVm != null){
                        List<Depot> depots = depotsContactVm.findDepotsOfKeyword(newText);
                        frag.updateData(depots);
                    }
                    frag.setFinding(false);
                } else if (!frag.isHidden()) {
                    getSupportFragmentManager().beginTransaction().hide(frag).commit();
                }
                return false;
            }
        });
    }

    @Override
    public void onDepotSelected(Depot place) {
        Log.i("Selected", place.getName());
        Intent intent = new Intent();
        intent.putExtra(KEY_GET_SELECTED_PLACE, place);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!searchFrag.isHidden()) {
            //Hide search fragment
            stopSearchView.setQuery(null, false);
            getSupportFragmentManager().beginTransaction().hide(searchFrag).commit();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onExceptionOccupied(@NonNull Call call, @NonNull Response response, final @NonNull Exception e) {
        progUpdateDepot.post(new Runnable() {
            @Override
            public void run() {
                progUpdateDepot.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(DepotSearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull Call call, final @NonNull IOException e) {
        progUpdateDepot.post(new Runnable() {
            @Override
            public void run() {
                progUpdateDepot.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(DepotSearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
