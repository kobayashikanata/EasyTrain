package com.per.epx.easytrain.models.getset;

import java.io.Serializable;

//A set property for program
public class Setter0<T> implements ISetter0<T>, Serializable {
    private T data;

    public Setter0(){}

    public Setter0(T def){
        this.data = def;
    }

    public void set(T value){
        this.data = value;
    }

    protected T get(){
        return this.data;
    }
}
