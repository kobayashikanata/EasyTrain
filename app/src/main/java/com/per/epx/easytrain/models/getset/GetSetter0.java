package com.per.epx.easytrain.models.getset;

import java.io.Serializable;

//A get-set property for program
public class GetSetter0<T> extends Setter0<T> implements ISetter0<T>, IGetter0<T>, Serializable{

    public GetSetter0(){ }

    public GetSetter0(T def){
        super(def);
    }

    public T get(){
        return super.get();
    }
}
