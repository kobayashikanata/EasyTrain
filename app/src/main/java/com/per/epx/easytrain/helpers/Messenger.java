package com.per.epx.easytrain.helpers;

import com.per.epx.easytrain.interfaces.IAction1;

public class Messenger {
    public static final Messenger DEFAULT = new Messenger();

    public <T> void register(Object recipient, Object token, IAction1<T> onMessage){

    }

    public <T> void register(Object recipient, IAction1<T> onMessage){

    }

    public void unregister(Object recipient, Object token){

    }

    public void unregister(Object recipient){

    }
}
