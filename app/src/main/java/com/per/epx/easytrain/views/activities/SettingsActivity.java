package com.per.epx.easytrain.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.per.epx.easytrain.App;
import com.per.epx.easytrain.R;
import com.per.epx.easytrain.helpers.LanguageUtil;
import com.per.epx.easytrain.helpers.PrefSettings;
import com.per.epx.easytrain.views.activities.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvServerAddress;
    private TextView tvLanguage;
    private TextView tvApiRedirectTips;
    private CheckedTextView ctvClearOnExit;
    private CheckedTextView ctvApiRedirect;
    private CheckedTextView ctvIsDebugging;
    private CheckedTextView ctvKeepDrawerOpen;
    private CheckedTextView ctvUseWideTemplate;

    private final List<String> langList = new ArrayList<>();
    private String serverAddress;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_content_settings;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        super.setupViews(savedInstanceState);
        ctvClearOnExit = findViewById(R.id.ctv_check_clear_on_exit);
        ctvApiRedirect = findViewById(R.id.ctv_check_using_local_api);
        ctvIsDebugging = findViewById(R.id.ctv_check_is_debug);
        ctvKeepDrawerOpen = findViewById(R.id.ctv_keep_drawer_open);
        ctvUseWideTemplate = findViewById(R.id.ctv_use_wide_template);
        findViewById(R.id.pane_check_clear_on_exit).setOnClickListener(this);
        findViewById(R.id.pane_check_api_redirect).setOnClickListener(this);
        findViewById(R.id.pane_check_is_debug).setOnClickListener(this);
        findViewById(R.id.pane_set_server).setOnClickListener(this);
        findViewById(R.id.pane_set_language).setOnClickListener(this);
        findViewById(R.id.pane_keep_drawer_open).setOnClickListener(this);
        findViewById(R.id.pane_use_wide_template).setOnClickListener(this);
        tvServerAddress = findViewById(R.id.tv_set_server_tips);
        tvLanguage = findViewById(R.id.tv_selected_language);
        tvApiRedirectTips = findViewById(R.id.tv_api_redirect_tips);
        langList.add(getString(R.string.lang_default_name));
        langList.add(getString(R.string.lang_en_name));
        langList.add(getString(R.string.lang_zh_name));
    }

    @Override
    protected void setupActionBar(Toolbar bar) {
        super.setupActionBar(bar);
        super.setTitle(R.string.tb_title_activity_settings);
    }

    @Override
    protected void handIntentAfterView(Intent intent) {
        super.handIntentAfterView(intent);

        ctvApiRedirect.setChecked(PrefSettings.isApiRedirect(this));
        ctvClearOnExit.setChecked(PrefSettings.isClearWhenExit(this));
        ctvIsDebugging.setChecked(PrefSettings.isDebugging(this));
        ctvKeepDrawerOpen.setChecked(PrefSettings.isDrawerKeepOpen(this));
        ctvUseWideTemplate.setChecked(PrefSettings.isWideTemplateUsed(this));
        tvLanguage.setText(langList.get(getSelectedLangIndex(App.getLanguage())));

        updateServerAddress(PrefSettings.getServerAddress(this));
        updateApiRedirectTips(ctvApiRedirect.isChecked());
    }

    private void updateApiRedirectTips(boolean isApiRedirect){
        tvApiRedirectTips.setText(isApiRedirect?R.string.settings_tips_using_local_api:
                R.string.default_settings_tips_using_remote_api);
    }

    private void updateServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        if(TextUtils.isEmpty(serverAddress)){
            tvServerAddress.setText(R.string.tips_server_address_is_not_set);
        }else{
            tvServerAddress.setText(serverAddress);
        }
    }

    private int getSelectedLangIndex(String selectedLang){
        int selectedIndex =  0;
        if(LanguageUtil.LanguageConstants.ENGLISH.equals(selectedLang)){
            selectedIndex = 1;
        }else if(LanguageUtil.LanguageConstants.SIMPLIFIED_CHINESE.equals(selectedLang)){
            selectedIndex = 2;
        }
        return selectedIndex;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pane_set_server:{
                new MaterialDialog.Builder(this)
                        .title(R.string.title_set_server_ip)
                        .content(R.string.title_set_server_ip)
                        .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                        .autoDismiss(false)
                        .input(getString(R.string.tips_input_server_address), serverAddress, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                String serverAddress = input.toString();
                                if(Patterns.WEB_URL.matcher(input).matches()){
                                    dialog.dismiss();
                                    Context context = SettingsActivity.this;
                                    updateServerAddress(serverAddress);
                                    PrefSettings.putServerAddress(context, serverAddress);
                                    App.updateServerUrl(context);
                                }else{
                                    if(dialog.getInputEditText() != null){
                                        dialog.getInputEditText().setError(getString(R.string.wrong_server_address_tips));
                                    }
                                }
                            }
                        }).show();
            }break;
            case R.id.pane_set_language:{
                final int finalSelectedIndex = getSelectedLangIndex(App.getLanguage());
                new MaterialDialog.Builder(this)
                        .title(R.string.title_change_language)
                        .items(langList)
                        .itemsCallbackSingleChoice(finalSelectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which != finalSelectedIndex){
                                    switch (which){
                                        case 0:{
                                            App.setLanguage(SettingsActivity.this, LanguageUtil.LanguageConstants.AUTO);
                                        }break;
                                        case 1:{
                                            App.setLanguage(SettingsActivity.this, LanguageUtil.LanguageConstants.ENGLISH);
                                        }break;
                                        case 2:{
                                            App.setLanguage(SettingsActivity.this, LanguageUtil.LanguageConstants.SIMPLIFIED_CHINESE);
                                        }break;
                                        default:return true;
                                    }
                                    SettingsActivity.this.recreate();
                                }
                                return true;
                            }
                        }).show();
            }break;
            case R.id.pane_check_api_redirect:{
                ctvApiRedirect.toggle();
                updateApiRedirectTips(ctvApiRedirect.isChecked());
                PrefSettings.putApiRedirect(this, ctvApiRedirect.isChecked());
            }break;
            case R.id.pane_check_clear_on_exit:{
                ctvClearOnExit.toggle();
                PrefSettings.putClearWhenExit(this, ctvClearOnExit.isChecked());
            }break;
            case R.id.pane_check_is_debug:{
                ctvIsDebugging.toggle();
                PrefSettings.putIsDebugging(this, ctvIsDebugging.isChecked());
                App.setDebugging(ctvIsDebugging.isChecked());
            }break;
            case R.id.pane_keep_drawer_open:{
                ctvKeepDrawerOpen.toggle();
                PrefSettings.putKeepDrawerOpen(this, ctvKeepDrawerOpen.isChecked());
                App.setDrawerKeepOpen(ctvKeepDrawerOpen.isChecked());
            }break;
            case R.id.pane_use_wide_template:{
                ctvUseWideTemplate.toggle();
                PrefSettings.putWideTemplateUse(this, ctvUseWideTemplate.isChecked());
                App.setWideTemplate(ctvUseWideTemplate.isChecked());
            }break;
        }
    }

    public boolean isValidIP(String addr) {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        String rexp = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|[1-9])\\."
                + "(25[0-5]|2[0-4]\\d|1\\d{1,2}|\\d{2}|\\d)\\."
                + "(25[0-5]|2[0-4]\\d|1\\d{1,2}|\\d{2}|\\d)\\."
                + "(25[0-5]|2[0-4]\\d|1\\d{1,2}|\\d{2}|\\d)";;
        return Pattern.compile(rexp).matcher(addr).find();
    }
}
