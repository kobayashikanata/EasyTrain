package com.per.epx.easytrain.views.activities.search.depot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.interfaces.DepotSelectedListener;
import com.per.epx.easytrain.models.Depot;

import java.util.List;

public class DepotSearchFragment extends Fragment {
    private ViewGroup searchProgress;
    private TextView mTvNoResult;
    private RecyclerView mRecyclerView;
    private RcvListAdapterBase<Depot> mAdapter;
    private DepotSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchProgress = view.findViewById(R.id.pane_search_progress);
        mTvNoResult = (TextView) view.findViewById(R.id.tv_no_result);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recy);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //mRecyclerView.setHasFixedSize(true);
        mAdapter = new RcvListAdapterBase<>(new Factory());
        mRecyclerView.setAdapter(mAdapter);
        searchProgress.setVisibility(View.GONE);
    }

    public void setFinding(boolean isFinding){
        if(isFinding){
            mTvNoResult.setVisibility(View.GONE);
            searchProgress.setVisibility(View.VISIBLE);
        }else{
            searchProgress.setVisibility(View.GONE);
        }
    }

    public void updateData(List<Depot> data){
        mAdapter.data().clear();
        if(data != null && data.size() > 0){
            mAdapter.data().addAll(data);
        }
        mAdapter.notifyDataSetChanged();
        if(mAdapter.data().size() == 0){
            mTvNoResult.setVisibility(View.VISIBLE);
        }else{
            mTvNoResult.setVisibility(View.GONE);
        }
    }

    public void setPlaceSelectedListener(DepotSelectedListener l) {
        this.listener = l;
    }

    public interface OnResultClicked{
        void onClicked(Depot place);
    }

    private class Factory implements RcvAdapterBase.IViewHolderFactory{
        @Override
        public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
            return new RcvAdapterBase.VHLayoutBase<Depot>(parent.getContext(), R.layout.template_item_depot_whole_line) {
                @Override
                protected void onBindData(IHolderApi holderApi, final Depot data, RcvAdapterBase.Payload payload) {
                    holderApi.setText(R.id.tv_place_name, data.getName());
                    holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                        @Override
                        public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                            listener.onDepotSelected(mAdapter.data().get(getAdapterPosition()));
                            //ToastUtil.showShort(getContext(), "选择了" + resultItems.get(position).getName());
                        }
                    });
                }
            };
        }
    }

    /*private TimeInterpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private void focusOn(View v, View movableView, boolean animated) {
        v.getDrawingRect(mTmpRect);
        mMainContainer.offsetDescendantRectToMyCoords(v, mTmpRect);
        movableView.animate().
                translationY(-mTmpRect.top).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setInterpolator(ANIMATION_INTERPOLATOR).
                setListener(new LayerEnablingAnimatorListener(movableView)).
                start();
    }

    private void unfocus(View v, View movableView, boolean animated) {
        movableView.animate().
                translationY(0).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setInterpolator(ANIMATION_INTERPOLATOR).
                setListener(new LayerEnablingAnimatorListener(movableView)).
                start();
    }

    private void fadeOutToBottom(View v, boolean animated) {
        v.animate().
                translationYBy(mHalfHeight).
                alpha(0).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setInterpolator(ANIMATION_INTERPOLATOR).
                setListener(new LayerEnablingAnimatorListener(v)).
                start();
    }

    private void fadeInToTop(View v, boolean animated) {
        v.animate().
                translationYBy(-mHalfHeight).
                alpha(1).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setInterpolator(ANIMATION_INTERPOLATOR).
                setListener(new LayerEnablingAnimatorListener(v)).
                start();
    }

    private void slideInToTop(View v, boolean animated) {
        v.animate().
                translationY(0).
                alpha(1).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setListener(new LayerEnablingAnimatorListener(v)).
                setInterpolator(ANIMATION_INTERPOLATOR);
    }

    private void slideOutToBottom(View v, boolean animated) {
        v.animate().
                translationY(mHalfHeight * 2).
                alpha(0).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setListener(new LayerEnablingAnimatorListener(v)).
                setInterpolator(ANIMATION_INTERPOLATOR);
    }

    private void stickTo(View v, View viewToStickTo, boolean animated) {
        v.getDrawingRect(mTmpRect);
        mMainContainer.offsetDescendantRectToMyCoords(v, mTmpRect);

        v.animate().
                translationY(viewToStickTo.getHeight() - mTmpRect.top).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setInterpolator(ANIMATION_INTERPOLATOR).
                start();
    }

    private void unstickFrom(View v, View viewToStickTo, boolean animated) {
        v.animate().
                translationY(0).
                setDuration(animated ? ANIMATION_DURATION : 0).
                setInterpolator(ANIMATION_INTERPOLATOR).
                setListener(new LayerEnablingAnimatorListener(viewToStickTo)).
                start();
    }

    private ActionMode.Callback mCallback = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            mode.setTitle("From");

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            slideOutToBottom(mEditorLayout, true);

            switch (mCurrentSelectedViewId) {
                case R.id.btn_departure:
                    unstickFrom(mFirstSpacer, mDepatureBtn, true);
                    fadeInToTop(mPassengersContainer, true);
                    fadeInToTop(mSearchBtn, true);
                    fadeInToTop(mThirdSpacer, true);
                    unfocus(mDepatureBtn, mStationsContainer, true);
                    unfocus(mDepatureBtn, mDateTimeContainer, true);
                    unfocus(mDepatureBtn, mSencondSpacer, true);

                    mDepatureEt.setClickable(false);
                    mDepatureEt.setFocusable(false);
                    mDepatureBtn.setVisibility(View.VISIBLE);
                    mInputManager.hideSoftInputFromWindow(mDepatureEt.getWindowToken(), 0);
                    break;
                case R.id.btn_arrival:
                    unstickFrom(mFirstSpacer, mArrivalBtn, true);
                    fadeInToTop(mPassengersContainer, true);
                    fadeInToTop(mSearchBtn, true);
                    fadeInToTop(mThirdSpacer, true);
                    unfocus(mArrivalBtn, mStationsContainer, true);
                    unfocus(mArrivalBtn, mDateTimeContainer, true);
                    unfocus(mArrivalBtn, mSencondSpacer, true);

                    mArrivalEt.setClickable(false);
                    mArrivalEt.setFocusable(false);
                    mArrivalBtn.setVisibility(View.VISIBLE);
                    mInputManager.hideSoftInputFromWindow(mArrivalEt.getWindowToken(), 0);
                    break;
                case R.id.outward:

                    unstickFrom(mSencondSpacer, mOutwardTv, true);
                    fadeInToTop(mPassengersContainer, true);
                    fadeInToTop(mSearchBtn, true);
                    fadeInToTop(mThirdSpacer, true);
                    unfocus(mOutwardTv, mStationsContainer, true);
                    unfocus(mOutwardTv, mDateTimeContainer, true);
                    unfocus(mOutwardTv, mFirstSpacer, true);

                    break;
                case R.id.inward:

                    unstickFrom(mSencondSpacer, mInwardTv, true);
                    fadeInToTop(mPassengersContainer, true);
                    fadeInToTop(mSearchBtn, true);
                    fadeInToTop(mThirdSpacer, true);
                    unfocus(mInwardTv, mStationsContainer, true);
                    unfocus(mInwardTv, mDateTimeContainer, true);
                    unfocus(mInwardTv, mFirstSpacer, true);

                    break;
            }
        }
    };*/
}