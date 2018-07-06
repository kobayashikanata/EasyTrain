package com.per.epx.easytrain.views.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.ActionHelper;
import com.per.epx.easytrain.helpers.CommonHelper;
import com.per.epx.easytrain.helpers.DateFormatter;
import com.per.epx.easytrain.helpers.RouteAskValidator;
import com.per.epx.easytrain.helpers.SmoothActionBarDrawerToggle;
import com.per.epx.easytrain.helpers.history.AskHistoryHelper;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.models.Depot;
import com.per.epx.easytrain.models.history.SolutionAskHistory;
import com.per.epx.easytrain.models.getset.GetSetter0;
import com.per.epx.easytrain.models.getset.ISetter0;
import com.per.epx.easytrain.models.req.SolutionAsk;
import com.per.epx.easytrain.testing.ApiRedirect;
import com.per.epx.easytrain.viewmodels.DepotsContactViewModel;
import com.per.epx.easytrain.viewmodels.SearchViewModel;
import com.per.epx.easytrain.viewmodels.ViewModelHelper;
import com.per.epx.easytrain.views.activities.base.BaseActivity0;
import com.per.epx.easytrain.views.activities.search.advanced.AdvancedSearchActivity;
import com.per.epx.easytrain.views.activities.search.depot.DepotSearchActivity;
import com.per.epx.easytrain.views.activities.solution.SolutionAskHistoryActivity;
import com.per.epx.easytrain.views.activities.solution.SolutionsMasterActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends BaseActivity0 implements DatePickerDialog.OnDateSetListener{
    public static final int CODE_REQ_FROM_PLACE = 22;
    public static final int CODE_REQ_TO_PLACE = 33;

    public static final String KEY_PUT_PRE_FILL_FROM = "KEY_PUT_PRE_FILL_FROM";
    public static final String KEY_PUT_PRE_FILL_TO = "KEY_PUT_PRE_FILL_TO";
    private static final String TAG_SHOW_DATE_DIALOG = "date-picker-dialog";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private Toolbar toolbar;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvDate;
    //private TextView tvHourMinute;
    private SmoothActionBarDrawerToggle drawerToggle;

    //View models
    private SearchViewModel searchVm;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected Toolbar getToolBar() {
        return findViewById(R.id.toolbar_main);
    }

    @Override
    protected void beforeSetupViews() {
        super.beforeSetupViews();
        EventBus.getDefault().register(this);
        //Setup view-models
        searchVm = new SearchViewModel();
    }

    @SuppressWarnings("unused")
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        //Setup drawer
        final DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawerToggle = new SmoothActionBarDrawerToggle(this, drawer, getToolBar(), R.string.app_name, R.string.app_name);
        //Connect drawer
        drawerToggle.syncState();
        drawer.addDrawerListener(drawerToggle);
        //Setup navigationView listeners
        NavigationView nav = (NavigationView)findViewById(R.id.nav_main);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intentForActivity = null;
                switch (item.getItemId()){
                    case R.id.menu_user:
                        intentForActivity = new Intent(MainActivity.this, LoginActivity.class);
                        break;
                    case R.id.menu_about:
                        intentForActivity = new Intent(MainActivity.this, AboutActivity.class);
                        break;
                    case R.id.menu_history:
                        intentForActivity = new Intent(MainActivity.this, SolutionAskHistoryActivity.class);
                        break;
                    case R.id.menu_settings:
                        intentForActivity = new Intent(MainActivity.this, SettingsActivity.class);
                        break;
                    case R.id.menu_testing:
                        break;
                }
                if(intentForActivity == null){
                    drawer.closeDrawers();
                }else if(App.isDrawerKeepOpen()){
                    startActivity(intentForActivity);
                }else{
                    final Intent finalRun = intentForActivity;
                    drawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(finalRun);
                        }
                    });
                    drawer.closeDrawers();
                }
                return false;
            }
        });
        NavTabListener navTabListener = new NavTabListener();
        findViewById(R.id.iv_show_history).setOnClickListener(navTabListener);
        findViewById(R.id.iv_open_like).setOnClickListener(navTabListener);
        findViewById(R.id.iv_line_search).setOnClickListener(navTabListener);
        //Link action
        final VmLinker linker = new VmLinker(searchVm);
        linker.onSubmit.set(new IAction1<SearchViewModel>() {
            @Override
            public void invoke(SearchViewModel data) {
                AskHistoryHelper.getInstance().append(MainActivity.this, new SolutionAskHistory(data.model.from.get(), data.model.to.get()));
                Intent intent = new Intent(MainActivity.this, SolutionsMasterActivity.class);
                intent.putExtra(SolutionsMasterActivity.KEY_PUT_SEARCH_MODEL, (Parcelable)data.model);
                MainActivity.this.startActivity(intent);
            }
        });
        tvFrom = findViewById(R.id.tv_from);
        tvTo = findViewById(R.id.tv_to);
        findViewById(R.id.iv_switch_begin_terminal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Depot from = linker.ownVm.model.from.get();
                linker.ownVm.model.from.set(linker.ownVm.model.to.get());
                linker.ownVm.model.to.set(from);
            }
        });

        linker.ownVm.model.from.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(linker.ownVm.model.from.get() == null){
                    tvFrom.setText("");
                }else {
                    tvFrom.setText(linker.ownVm.model.from.get().getName());
                }
            }
        });
        linker.ownVm.model.to.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(linker.ownVm.model.to.get() == null){
                    tvFrom.setText("");
                }else{
                    tvTo.setText(linker.ownVm.model.to.get().getName());
                }
            }
        });
        findViewById(R.id.tv_search).setOnClickListener(linker.submitValidListener);
        (tvDate = findViewById(R.id.tv_date)).addTextChangedListener(linker.onDateTextChanged);
        tvDate.setText(DateFormatter.formatDefault(new Date(), "yyyy-MM-dd"));
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long defaultSelected = linker.ownVm.model.dateMs.get();
                if(defaultSelected <= 0){
                    defaultSelected = System.currentTimeMillis();
                }
                Calendar selected = Calendar.getInstance();
                selected.setTimeInMillis(defaultSelected);
                Calendar now = Calendar.getInstance();
                Calendar max = Calendar.getInstance();
                max.add(Calendar.MONTH, 2);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        selected.get(Calendar.YEAR),
                        selected.get(Calendar.MONTH),
                        selected.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.setMaxDate(max);
                dpd.show(getFragmentManager(), TAG_SHOW_DATE_DIALOG);
            }
        });
        tvFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, DepotSearchActivity.class),
                        CODE_REQ_FROM_PLACE);
            }
        });
        tvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, DepotSearchActivity.class),
                        CODE_REQ_TO_PLACE);
            }
        });
        //Bind to type checked value
        RadioGroup typeGroup;
        (typeGroup = findViewById(R.id.rg_type)).setOnCheckedChangeListener(linker.onTypeChanged);
        linker.onTypeChanged.onCheckedChanged(typeGroup, typeGroup.getCheckedRadioButtonId());
        //For raising text changed event for initializing
        tvDate.setText(tvDate.getText());

        ApiRedirect.getDepotsContact(this.hashCode(), new IAction1<DepotsContactViewModel>() {
            @Override
            public void invoke(DepotsContactViewModel data) {
                if(data == null) return;
                List<Depot> hotDepots = data.getHotDepots();
                if(hotDepots != null && hotDepots.size() > 1){
                    if(linker.ownVm.model.from.get() == null &&
                            linker.ownVm.model.to.get() == null){
                        linker.ownVm.model.from.set(hotDepots.get(0));
                        linker.ownVm.model.to.set(hotDepots.get(1));
                    }
                }
            }
        });

        //Testing
//        String from = "AAA";
//        String to = "ZZZ";
//        searchVm.model.from.set(new Depot(0, from, from, from, from));
//        searchVm.model.to.set(new Depot(1, to, to, to, to));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handIntentAfterView(intent);
    }

    @Override
    protected void handIntentAfterView(Intent intent) {
        super.handIntentAfterView(intent);
        if(intent != null){
            Depot from = CommonHelper.safeCastSerializable(intent, KEY_PUT_PRE_FILL_FROM, Depot.class);
            Depot to = CommonHelper.safeCastSerializable(intent, KEY_PUT_PRE_FILL_TO, Depot.class);
            if(from != null){
                tvFrom.setText(from.getName());
                searchVm.model.from.set(from);
            }
            if(to != null) {
                tvTo.setText(to.getName());
                searchVm.model.to.set(to);
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostRefreshEvents(App.LanguageChangeEvent event){
        this.recreate();
    }

    //TODO Support saving and restoring state
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ViewModelHelper.cleanUpVmsSafely(searchVm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(TAG_SHOW_DATE_DIALOG);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        tvDate.setText(dateFormatter.format(c.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            return drawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CODE_REQ_FROM_PLACE:{
                    Serializable o = data.getSerializableExtra(DepotSearchActivity.KEY_GET_SELECTED_PLACE);
                    if(o != null && o instanceof Depot){
                        searchVm.model.from.set((Depot)o);
                    }
                }break;
                case CODE_REQ_TO_PLACE:{
                    Serializable o = data.getSerializableExtra(DepotSearchActivity.KEY_GET_SELECTED_PLACE);
                    if(o != null && o instanceof Depot){
                        searchVm.model.to.set((Depot)o);
                    }
                }break;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class VmLinker {
        private final SearchViewModel ownVm;
        private final GetSetter0<IAction1<SearchViewModel>> onSubmit0 = new GetSetter0<>();
        public final ISetter0<IAction1<SearchViewModel>> onSubmit = onSubmit0;
        public VmLinker(SearchViewModel vm){
            this.ownVm = vm;
        }

        public final TextWatcher onDateTextChanged = new TextWatcher1(){
            private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            @Override
            public void afterTextChanged(Editable s) {
                Date d = null;
                try{
                    d = format.parse(s.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(d != null){
                    ownVm.model.dateMs.set(d.getTime());
                }
            }
        };

        public final RadioGroup.OnCheckedChangeListener onTypeChanged = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group.getParent() instanceof HorizontalScrollView){
                    //Get views
                    HorizontalScrollView hsv = (HorizontalScrollView)group.getParent();
                    RadioButton rb = group.findViewById(checkedId);
                    //Calculate size
                    DisplayMetrics d = group.getContext().getResources().getDisplayMetrics();
                    int halfScreen = d.widthPixels / 2;
                    int leftScreen = rb.getLeft() - hsv.getScrollX();
                    //Scroll HorizontalScrollView
                    hsv.smoothScrollBy((leftScreen-halfScreen), 0);
                }
                switch (checkedId){
                    case R.id.rb_cheapest:{
                        ownVm.model.type.set(SolutionAsk.CHEAPEST);
                    }break;
                    case R.id.rb_shortest:{
                        ownVm.model.type.set(SolutionAsk.SHORTEST);
                    }break;
                    case R.id.rb_least_time:{
                        ownVm.model.type.set(SolutionAsk.LEAST_TIME);
                    }break;
                    case R.id.rb_least_transfer:{
                        ownVm.model.type.set(SolutionAsk.LEAST_TRANSFER);
                    }break;
                    default:break;
                }
            }
        };

        public final View.OnClickListener submitValidListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SolutionAsk ask = ownVm.wrapCurrentAsk();
                @RouteAskValidator.Type int valid = ownVm.check(ask);
                if(valid != RouteAskValidator.PASS){
                    Snackbar.make(v, RouteAskValidator.getMessage(v.getContext(), valid), Snackbar.LENGTH_SHORT).show();
                }else{
                    if(CommonHelper.isNetworkAvailable(v.getContext())){
                        ActionHelper.safelyInvoke(onSubmit0.get(), ownVm);
                    }else{
                        Snackbar.make(v, R.string.tip_ex_network_no_available, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        };


    }

    private static class NavTabListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            switch (v.getId()){
                case R.id.iv_line_search:
                    context.startActivity(new Intent(context, AdvancedSearchActivity.class));
                    break;
                case R.id.iv_open_like:
                    context.startActivity(new Intent(context, FavoriteActivity.class));
                    break;
                case R.id.iv_show_history:
                    context.startActivity(new Intent(context, SolutionAskHistoryActivity.class));
                    break;
                default:break;
            }
        }
    }


    private static class TextWatcher1 implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
