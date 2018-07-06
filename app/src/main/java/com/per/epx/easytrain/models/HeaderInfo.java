package com.per.epx.easytrain.models;

import android.support.annotation.DrawableRes;

public class HeaderInfo {
    private String title;
    private String subtitle;
    private @DrawableRes
    int backgroundResource;

    public HeaderInfo() {
    }

    public HeaderInfo(String title, String subtitle, int backgroundResource) {
        this.title = title;
        this.subtitle = subtitle;
        this.backgroundResource = backgroundResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getBackgroundResource() {
        return backgroundResource;
    }

    public void setBackgroundResource(int backgroundResource) {
        this.backgroundResource = backgroundResource;
    }
}
