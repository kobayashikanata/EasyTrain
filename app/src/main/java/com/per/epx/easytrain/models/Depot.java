package com.per.epx.easytrain.models;

import java.io.Serializable;

public class Depot extends DepotLite {
    private String english;
    private String pinyin;

    public Depot() {
    }

    public Depot(long id, String code, String name) {
        super(id, code, name);
    }

    public Depot(long id, String code, String name, String english, String pinyin) {
        super(id, code, name);
        this.english = english;
        this.pinyin = pinyin;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

}
