package com.per.epx.easytrain.helpers;

import com.per.epx.easytrain.interfaces.IAction0;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.IAction2;

public class ActionHelper {
    public static void safelyInvoke(IAction0 action){
        if(action != null){
            action.invoke();
        }
    }
    public static <T> void safelyInvoke(IAction1<T> action, T data){
        if(action != null){
            action.invoke(data);
        }
    }
    public static <T1, T2> void safelyInvoke(IAction2<T1, T2> action, T1 d1, T2 d2){
        if(action != null){
            action.invoke(d1, d2);
        }
    }
}
