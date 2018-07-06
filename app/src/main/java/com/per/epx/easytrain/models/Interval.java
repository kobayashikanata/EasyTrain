package com.per.epx.easytrain.models;

import com.per.epx.easytrain.models.getset.GetSetter0;

import java.io.Serializable;

public class Interval<T> implements Serializable {
    public final GetSetter0<T> from = new GetSetter0<>();
    public final GetSetter0<T> to = new GetSetter0<>();

    public Interval(){}

    public Interval(T fromValue, T toValue){
        from.set(fromValue);
        to.set(toValue);
    }
}
