package com.per.epx.easytrain.models;

import com.per.epx.easytrain.models.sln.Slice;

import java.util.List;

public class EntireLine {
    private String code;
    private String name;
    private List<Slice> passes;

    public EntireLine() {
    }

    public EntireLine(String code, String name, List<Slice> passes) {
        this.code = code;
        this.name = name;
        this.passes = passes;
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

    public List<Slice> getPasses() {
        return passes;
    }

    public void setPasses(List<Slice> passes) {
        this.passes = passes;
    }
}
