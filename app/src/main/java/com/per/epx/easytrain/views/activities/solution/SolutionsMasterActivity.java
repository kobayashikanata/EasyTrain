package com.per.epx.easytrain.views.activities.solution;

import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.per.epx.darkerpopup.BackgroundDarkPopupWindow;
import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.adapters.base.IHolderApi;
import com.per.epx.easytrain.adapters.base.RcvAdapterBase;
import com.per.epx.easytrain.adapters.base.RcvListAdapterBase;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.ExceptionMessageHelper;
import com.per.epx.easytrain.helpers.RouteAskValidator;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.Interval;
import com.per.epx.easytrain.models.SearchModel;
import com.per.epx.easytrain.models.req.SolutionReply;
import com.per.epx.easytrain.models.sln.LineRoute;
import com.per.epx.easytrain.models.wrapper.RouteWrapper;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.viewmodels.RouteDataViewModel;
import com.per.epx.easytrain.viewmodels.SearchViewModel;
import com.per.epx.easytrain.views.activities.base.BaseActivity0;
import com.per.epx.easytrain.views.helpers.CheckPair;
import com.per.epx.easytrain.views.helpers.YAnimatedHidingScrollListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

public class SolutionsMasterActivity extends BaseActivity0 implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, Interceptor {
    public static final String KEY_PUT_REPLY_RESULTS = "aha,something here";
    public static final String KEY_PUT_SEARCH_MODEL = "oops,put-search-model-here";
    private static final String TAG_SHOW_DATE_DIALOG = "date-picker-dialog";
    private static final int ASK_KEY = 10003;

    private RcvListAdapterBase<RouteWrapperEx> adapter;

    private View emptyView;
    private View errorView;
    private SwipeRefreshLayout slnsRefreshLayout;
    private TextView tvPreviousDay;
    private TextView tvBySelectDay;
    //TODO Get max day set set this element disable depend on selected day
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private TextView tvNextDay;
    private CheckPair durationAndPay;
    private CheckPair driveAndArrive;
    private Handler postHandler = null;

    private SearchViewModel searchVm;
    private final RouteDataViewModel routeVm = new RouteDataViewModel();
    private final SparseIntArray idToSortType = new SparseIntArray();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_solutions;
    }

    @Override
    protected Toolbar getToolBar() {
        return findViewById(R.id.toolbar_solutions);
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setTitleView(R.id.tv_title_solutions);
        super.setTitle(R.string.tb_title_activity_slns);
        postHandler = new Handler();
        //Setup SwipeRefreshLayout
        errorView = findViewById(R.id.pane_on_error);
        emptyView = findViewById(R.id.view_empty_slns);
        slnsRefreshLayout = findViewById(R.id.srl_refresh_slns);
        slnsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search(searchVm, getWindow().getDecorView());
            }
        });
        //Get views for sorting
        final CheckPair durationAndPay0 = new CheckPair(this, R.id.ctv_sort_duration, R.id.ctv_sort_pay);
        final CheckPair driveAndArrive0 = new CheckPair(this, R.id.ctv_sort_drive, R.id.ctv_sort_arrive);
        this.durationAndPay = durationAndPay0;
        this.driveAndArrive = driveAndArrive0;
        idToSortType.clear();
        idToSortType.append(R.id.ctv_sort_drive, RouteDataViewModel.SORT_DRIVE);
        idToSortType.append(R.id.ctv_sort_arrive, RouteDataViewModel.SORT_ARRIVE);
        idToSortType.append(R.id.ctv_sort_duration, RouteDataViewModel.SORT_DURATION);
        idToSortType.append(R.id.ctv_sort_pay, RouteDataViewModel.SORT_PAY);
        findViewById(R.id.vg_sort_duration_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uncheckedId = durationAndPay0.uncheckedOrDefaultId();
                driveAndArrive0.uncheckedAll();
                durationAndPay0.check(uncheckedId);
                routeVm.updateComparatorAndSort(idToSortType.get(uncheckedId));
                updateData();
            }
        });
        findViewById(R.id.vg_sort_drive_arrive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uncheckedId = driveAndArrive0.uncheckedOrDefaultId();
                durationAndPay0.uncheckedAll();
                driveAndArrive0.check(uncheckedId);
                routeVm.updateComparatorAndSort(idToSortType.get(uncheckedId));
                updateData();
            }
        });
        //Get views for changing date searching
        (tvNextDay = findViewById(R.id.tv_query_next_day)).setOnClickListener(this);
        (tvPreviousDay = findViewById(R.id.tv_query_prev_day)).setOnClickListener(this);
        (tvBySelectDay = findViewById(R.id.tv_query_change_date)).setOnClickListener(this);
        //Setup recyclerView
        final RecyclerView rcv = findViewById(R.id.rv_data);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        final int viewTypeEnd = 1;
        rcv.setAdapter((adapter = new RcvListAdapterBase<RouteWrapperEx>(new RcvAdapterBase.IViewHolderFactory(){
            private final @LayoutRes int slnMetaLayout = App.isWideTemplate() ?
                    R.layout.template_item_sln_meta_big :
                    R.layout.template_item_sln_meta;
            @Override
            public RcvAdapterBase.VHBase createViewHolder(ViewGroup parent, int viewType) {
                if(viewType == viewTypeEnd){
                    return new RcvAdapterBase.VHViewParentBase<RouteWrapperEx>(parent, R.layout.template_item_tips) {
                        @Override
                        protected void onBindData(IHolderApi holderApi, RouteWrapperEx data, RcvAdapterBase.Payload payload) {
                            holderApi.setText(R.id.tv_tips, data.getMessage());
                            holderApi.setVisibility(R.id.tv_tips, View.INVISIBLE);
                        }
                    };
                }
                return new RcvAdapterBase.VHViewParentBase<RouteWrapperEx>(parent, slnMetaLayout) {
                    @Override
                    protected void onBindData(IHolderApi holderApi, final RouteWrapperEx data, RcvAdapterBase.Payload payload) {
                        holderApi.setText(R.id.tv_pay, data.getWrapper().getPayment());
                        holderApi.setText(R.id.tv_transfer_size, data.getWrapper().getCrossSize());
                        holderApi.setText(R.id.tv_transfer_lines, data.getWrapper().getTransferText());
                        holderApi.setText(R.id.tv_begin_time, data.getWrapper().getBeginHourMinuteText());
                        holderApi.setText(R.id.tv_terminal_time, data.getWrapper().getTerminalHourMinuteText());
                        holderApi.setText(R.id.tv_begin_name, data.getWrapper().getBeginName());
                        holderApi.setText(R.id.tv_terminal_name, data.getWrapper().getTerminalName());
                        holderApi.setText(R.id.tv_used_time, data.getWrapper().getTimeUsageText());
                        holderApi.setOnItemClickListener(new IHolderApi.ItemClickListener() {
                            @Override
                            public void itemClicked(View view, int layoutPosition, int adapterPosition) {
                                Intent intent = new Intent(SolutionsMasterActivity.this, SolutionDetailActivity.class);
                                intent.putExtra(SolutionDetailActivity.KEY_PUT_SOLUTION, data.getWrapper().raw());
                                startActivity(intent);
                            }
                        });
                        if(data.getWrapper().getDayDiffer() > 0){
                            holderApi.setVisibility(R.id.tv_day_plus, View.VISIBLE);
                            holderApi.setText(R.id.tv_day_plus, String.format(Locale.getDefault(),  getString(R.string.over_day_format), data.getWrapper().getDayDiffer()));
                        }else{
                            holderApi.setVisibility(R.id.tv_day_plus, View.GONE);
                        }
                    }
                };
            }
        }){
            @Override
            public int getItemViewType(int position) {
                if(getItem(position) != null && getItem(position).isForEmpty()){
                    return viewTypeEnd;
                }
                return super.getItemViewType(position);
            }
        }));
        View bottomView = findViewById(R.id.layout_bottom_slns);
        rcv.setClipToPadding(true);
        rcv.setPadding(rcv.getPaddingLeft(), rcv.getPaddingRight(), rcv.getPaddingTop(), rcv.getPaddingBottom()+bottomView.getHeight());
        rcv.addOnScrollListener(new YAnimatedHidingScrollListener(bottomView));
        //Setup more click action
        final View btnMore = findViewById(R.id.tv_bottom_more);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAcceptedToAdapter(adapter, routeVm.getData());
                testPopupTimeRange(SolutionsMasterActivity.this, btnMore, rcv);
            }
        });
        findViewById(R.id.tv_btn_comparison).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SolutionsMasterActivity.this, SolutionSelectionActivity.class);
                List<RouteWrapper> wrappers = new ArrayList<>();
                for(RouteWrapperEx ex : adapter.data()){
                    if(ex.isForEmpty()) continue;
                    wrappers.add(ex.getWrapper());
                }
                intent.putExtra(SolutionSelectionActivity.KEY_PUT_SOLUTIONS, (Serializable) wrappers);
                startActivity(intent);
            }
        });
   }

    @Override
    protected void handIntentAfterView(Intent intent) {
        super.handIntentAfterView(intent);
        if(intent != null){
            SolutionReply reply = CommonHelper.safeCastSerializable(intent, KEY_PUT_REPLY_RESULTS, SolutionReply.class);
            if(adapter != null && reply != null){
                applySearchReplied(reply);
            }
            SearchModel sm = CommonHelper.safeCastParcelable(intent, KEY_PUT_SEARCH_MODEL, SearchModel.class);
            if(sm != null){
                searchVm = new SearchViewModel();
                searchVm.copyBasicModel(sm);
                searchVm.replied.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable observable, int i) {
                        postHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                slnsRefreshLayout.setRefreshing(false);
                                errorView.setVisibility(View.GONE);
                                applySearchReplied(searchVm.replied.get());
                            }
                        });
                    }
                });
                if(reply == null){
                    search(searchVm, getWindow().getDecorView());
                }
            }
        }
    }

    @Override
    public void onClick(final View v) {
        if(searchVm == null) return;
        switch (v.getId()) {
            case R.id.tv_query_change_date:{
                Calendar now = Calendar.getInstance();
                Calendar max = Calendar.getInstance();
                Calendar selected = Calendar.getInstance();
                if(searchVm.model.dateMs.get() != null){
                    selected.setTimeInMillis(searchVm.model.dateMs.get());
                }
                max.add(Calendar.MONTH, 2);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        selected.get(Calendar.YEAR),
                        selected.get(Calendar.MONTH),
                        selected.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.setMaxDate(max);
                dpd.show(getFragmentManager(), TAG_SHOW_DATE_DIALOG);
            }break;
            case R.id.tv_query_prev_day:{
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(searchVm.model.dateMs.get());
                c.add(Calendar.DAY_OF_YEAR, -1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                searchVm.model.dateMs.set(c.getTime().getTime());
                search(searchVm, v);
            }break;
            case R.id.tv_query_next_day:{
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(searchVm.model.dateMs.get());
                c.add(Calendar.DAY_OF_YEAR, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                searchVm.model.dateMs.set(c.getTime().getTime());
                search(searchVm, v);
            }break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        if(searchVm != null){
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            searchVm.model.dateMs.set(c.getTime().getTime());
            search(searchVm, getWindow().getDecorView());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApiRedirect.getApi().register(ASK_KEY, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(TAG_SHOW_DATE_DIALOG);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    protected void onDestroy() {
        ApiRedirect.getApi().unregister(this);
        routeVm.cleanUp();
        super.onDestroy();
    }

    @Override
    public void onExceptionOccupied(@NonNull Call call, @NonNull Response response, final @NonNull Exception e) {
        errorView.post(new Runnable() {
            @Override
            public void run() {
                errorView.setVisibility(View.VISIBLE);
                updateSetDateViews(new Date(searchVm.model.dateMs.get()));
                String message = ExceptionMessageHelper.getMessage(errorView.getContext(), e);
                ((TextView)errorView.findViewById(R.id.tv_common_error_msg)).setText(message);
                Toast.makeText(SolutionsMasterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull Call call, final @NonNull IOException e) {
        errorView.post(new Runnable() {
            @Override
            public void run() {
                errorView.setVisibility(View.VISIBLE);
                updateSetDateViews(new Date(searchVm.model.dateMs.get()));
                String message = ExceptionMessageHelper.getMessage(errorView.getContext(), e);
                ((TextView)errorView.findViewById(R.id.tv_common_error_msg)).setText(message);
                Toast.makeText(SolutionsMasterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private <T extends View> void search(SearchViewModel vm, T v){
        updateDepots(vm.model.from.get().getName(), vm.model.to.get().getName());
        updateSetDateViews(new Date(vm.model.dateMs.get()));
        errorView.setVisibility(View.GONE);
        slnsRefreshLayout.setRefreshing(true);
        tvNextDay.setEnabled(false);
        tvPreviousDay.setEnabled(false);
        tvBySelectDay.setEnabled(false);
        @RouteAskValidator.Type int valid = vm.find(ASK_KEY);
        if(valid != RouteAskValidator.PASS){
            Snackbar.make(v, RouteAskValidator.getMessage(v.getContext(), valid), Snackbar.LENGTH_SHORT).show();
            updateSetDateViews(new Date(vm.model.dateMs.get()));
            slnsRefreshLayout.setRefreshing(false);
        }
    }

    private void updateDepots(String fromName, String toName){
        super.setTitle(fromName + "-" + toName);
    }

    private void updateSetDateViews(Date date){
        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 2);
        String dateText = DateFormatter.formatMD(date);
        if(CommonHelper.isSameDay(date, new Date())){
            dateText += " " + getString(R.string.label_today);
        }
        tvPreviousDay.setEnabled(!CommonHelper.isSameDay(date, new Date()));
        tvNextDay.setEnabled(!CommonHelper.isSameDay(date, max.getTime()));
        tvBySelectDay.setEnabled(true);
        tvBySelectDay.setText(dateText);
    }

    //TODO On search value callback
    private void applySearchReplied(SolutionReply reply) {
        if(reply != null){
            errorView.setVisibility(View.GONE);
            if(reply.getReplyCode() != 200){
                emptyView.setVisibility(View.VISIBLE);
                updateSetDateViews(new Date(reply.getTripDate()));
            }else{
                try{
                    updateDepots(reply.getFrom().getName(), reply.getTo().getName());
                    //Get out date
                    updateSetDateViews(new Date(reply.getTripDate()));
                    routeVm.initialize(reply.getPlans());
                    addAcceptedToAdapter(adapter, routeVm.getData());
                    if(routeVm.getData() == null || routeVm.getData().size() == 0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else{
                        emptyView.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    errorView.setVisibility(View.VISIBLE);
                    ((TextView)errorView.findViewById(R.id.tv_common_error_msg)).setText(R.string.tips_recv_data_error);
                    Toast.makeText(SolutionsMasterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            //Restore check state
            if(durationAndPay != null){
                durationAndPay.uncheckedAll();
            }
            if(driveAndArrive != null){
                driveAndArrive.uncheckedAll();
            }
        }
    }

    private void updateData(){
        addAcceptedToAdapter(adapter, routeVm.getData());
    }

    private <T extends LineRoute> void addAcceptedToAdapter(RcvListAdapterBase<RouteWrapperEx> adapter, List<T> routes){
        adapter.data().clear();
        if(routes != null && routes.size() > 0){
            for(LineRoute route : routes){
                adapter.data().add(new RouteWrapperEx(route, this));
            }
        }
        adapter.data().add(new RouteWrapperEx(getString(R.string.tips_in_the_end)));
        adapter.notifyDataSetChanged();
    }

    private void setRangePinsByValue(RangeSeekBar rangeBar, Interval<Integer> interval){
        rangeBar.setValue(interval.from.get(), interval.to.get());
    }

    private void testPopupTimeRange(Context context, View showBase, View darkerAnimated){
        View view = LayoutInflater.from(context).inflate(R.layout.popuw_content_range_time, null, false);
        //Basic fields
        //final IRangeBarFormatter timeFormatter = new TimeFormatter();
        String noLimitText = context.getString(R.string.tips_no_limit);
        final TimeRangeChangeListener driveRangListener = new TimeRangeChangeListener(routeVm.getDriveMinutesRange(),noLimitText);
        final TimeRangeChangeListener arriveRangListener = new TimeRangeChangeListener(routeVm.getArriveMinutesRange(),noLimitText);
        final TimeRangeChangeListener transferRangListener = new TimeRangeChangeListener(routeVm.getTransferMinutesRange(),noLimitText,
                getString(R.string.label_hour), getString(R.string.label_minutes));
        RangeSeekBar driveRange = view.findViewById(R.id.rb_out_time_24h);
        RangeSeekBar arriveRange = view.findViewById(R.id.rb_arrive_time_24h);
        RangeSeekBar transferRange = view.findViewById(R.id.rb_transfer_time_24h);

        //arriveRange.setFormatter(timeFormatter);
        //driveRange.setFormatter(timeFormatter);
        //transferRange.setFormatter(timeFormatter);
        driveRangListener.setTextViews((TextView)view.findViewById(R.id.tv_trip_from),
                (TextView)view.findViewById(R.id.tv_trip_to));
        arriveRangListener.setTextViews((TextView)view.findViewById(R.id.tv_arrive_from),
                (TextView)view.findViewById(R.id.tv_arrive_to));
        transferRangListener.setTextViews((TextView)view.findViewById(R.id.tv_transfer_from),
                (TextView)view.findViewById(R.id.tv_transfer_to));
        setRangePinsByValue(driveRange, routeVm.getDriveMinutesRange());
        setRangePinsByValue(arriveRange, routeVm.getArriveMinutesRange());
        setRangePinsByValue(transferRange, routeVm.getTransferMinutesRange());
        //Attach listener
        arriveRange.setOnRangeChangedListener(arriveRangListener);
        driveRange.setOnRangeChangedListener(driveRangListener);
        transferRange.setOnRangeChangedListener(transferRangListener);

        //Make window
        final BackgroundDarkPopupWindow mPopupWindow = new BackgroundDarkPopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        //Setup listeners
        view.findViewById(R.id.tv_cancel_range).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        view.findViewById(R.id.tv_apply_range).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeVm.updateTimeRange(driveRangListener.range,
                        arriveRangListener.range,
                        transferRangListener.range);
                mPopupWindow.dismiss();
                routeVm.updateAccept();
                addAcceptedToAdapter(adapter, routeVm.getData());
            }
        });

        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        mPopupWindow.setDarkStyle(-1);
        mPopupWindow.setDarkColor(Color.parseColor("#a0000000"));
        mPopupWindow.resetDarkPosition();
        mPopupWindow.drakFillView(darkerAnimated);
        mPopupWindow.setAnimationStyle(R.style.BottomAnimated);
        mPopupWindow.showAtLocation(showBase, Gravity.BOTTOM, 0, 0);
    }

//    private static class TimeFormatter implements IRangeBarFormatter {
//        @Override
//        public String format(String value) {
//            try{
//                int totalMinutes = Integer.parseInt(value);
//                int hours = totalMinutes / 60;
//                int minutes = totalMinutes % 60;
//                return hours +":"+getMinuteText(minutes);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return value;
//        }
//
//        private String getMinuteText(int minutes){
//            if(minutes < 10){
//                return "0"+ minutes;
//            }
//            return String.valueOf(minutes);
//        }
//    }

    private static class RouteWrapperEx{
        private boolean forEmpty;
        private String message;
        private RouteWrapper wrapper;
        public RouteWrapperEx(LineRoute raw, Context context){
            this.wrapper = new RouteWrapper(raw, context);
        }

        public RouteWrapperEx(RouteWrapper wrapper){
            this.wrapper = wrapper;
        }

        public RouteWrapperEx(String message) {
            this.forEmpty = true;
            this.message = message;
        }

        public RouteWrapper getWrapper() {
            return wrapper;
        }

        public boolean isForEmpty() {
            return forEmpty;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @SuppressWarnings("unused")
    private static class TimeRangeChangeListener implements OnRangeChangedListener{
        public final Interval<Integer> range = new Interval<>();
        private TextView tvFrom;
        private TextView tvTo;
        private String noLimitText;
        private String hourText;
        private String minuteText;

        @SuppressWarnings("WeakerAccess")
        public TimeRangeChangeListener(String noLimitText){
            this.noLimitText = noLimitText;
        }
        public TimeRangeChangeListener(Interval<Integer> interval, String noLimitText){
            this(interval.from.get(), interval.to.get(), noLimitText, ":", "");
        }
        public TimeRangeChangeListener(Interval<Integer> interval, String noLimitText, String hourText, String minuteText){
            this(interval.from.get(), interval.to.get(), noLimitText, hourText, minuteText);
        }
        public TimeRangeChangeListener(int fromValue, int toValue, String noLimitText, String hourText, String minuteText){
            range.from.set(fromValue);
            range.to.set(toValue);
            this.noLimitText = noLimitText;
            this.hourText = hourText;
            this.minuteText = minuteText;
        }

        public void setTextViews(TextView tvFrom, TextView tvTo) {
            this.tvFrom = tvFrom;
            this.tvTo = tvTo;
            tvFrom.setText(format(range.from.get()));
            tvTo.setText(format(range.to.get()));
        }

        @Override
        public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
            range.from.set((int)min);
            range.to.set((int)max);
            String fromText = format(range.from.get());
            String toText = format(range.to.get());
            view.getLeftSeekBar().setIndicatorText(fromText);
            view.getRightSeekBar().setIndicatorText(toText);
            if(tvFrom != null){
                tvFrom.setText(fromText);
            }
            if(tvTo != null){
                tvTo.setText(toText);
            }
        }

        public String format(int totalMinutes) {
            if(totalMinutes >= 1440){
                return noLimitText;
            }
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            return hours + hourText +getMinuteText(minutes)+minuteText;
        }

        private String getMinuteText(int minutes){
            if(minutes < 10){
                return "0"+ minutes;
            }
            return String.valueOf(minutes);
        }


        @Override
        public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

        }

        @Override
        public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

        }
    }


}
