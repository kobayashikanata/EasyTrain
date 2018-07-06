package com.per.epx.easytrain.models.getset;

//A get property for program
public class Getter0<T> implements IGetter0<T>{
    private T data;
    public Getter0(T data){
        this.data = data;
    }

    public T get() {
        return data;
    }
}
