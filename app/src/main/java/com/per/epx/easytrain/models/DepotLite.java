package com.per.epx.easytrain.models;

import java.io.Serializable;

public class DepotLite implements Serializable{
    private long id;
    private String code;//The unique abbreviated of name
    private String name;

    public DepotLite() {
    }

    public DepotLite(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void idToCodeIfNull(){
        if(getCode() == null || getCode().length() == 0){
            this.code = String.valueOf(getId());
        }
    }
}
