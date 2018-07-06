package com.per.epx.easytrain.helpers;


import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.per.epx.easytrain.interfaces.IAction1;

/**
 * Created by daijian
 * head -> process1 -> process2 -> ... -> tail
 * Chain function combiner
 * It wrap a function can be invoked.
 * And you can combine functions you want by it's chain
 * When you invoke any Promise,
 * It will find the head then invoke whole functions in current chain by order.
 * It can only keep the process run by order which is managed by IAction
 * When you let some other process outside the IAction, Promise can not manage it.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Promise {
    private Promise previous;
    private Promise next;//Complete to next
    private Promise fail;//Not complete to fail.
    private IAction1<Promise> action;//The action to be invoked.
    private IAction1<InterruptData> globalInterrupted;//The action when Promise is interrupted.
    private IAction1<InterruptData> currentInterrupted;//The action when Promise is interrupted.

    private boolean fireNextAuto;//Fire next Promise auto for blocking function
    private boolean isCancelled;//Means if current Promise is cancelled.
    private boolean isInterrupted;//Means if current Promise is interrupted.
    private Object result;//The result of current Promise

    //TODO Use IAction<InterruptData> when interrupted.
    //链式中断的时候携带的数据类
    public class InterruptData {
        private boolean isBroadcastNeed = true;
        private Object result;

        public InterruptData(Object result) {
            this(result, true);
        }

        public InterruptData(Object result, boolean isBroadcastNeed) {
            this.result = result;
            this.isBroadcastNeed = isBroadcastNeed;
        }

        public Object getResult() {
            return result;
        }

        public boolean isBroadcastNeed() {
            return isBroadcastNeed;
        }

        public void setBroadcastNeed(boolean broadcastNeed) {
            isBroadcastNeed = broadcastNeed;
        }
    }

    //获取前一个Promise
    public Promise previous() {
        return previous;
    }

    //当前Promise的执行结果
    public Object result() {
        return result;
    }


    //当前Promise按照同步执行直接自动调用下一个Promise
    @CheckResult(suggest = "invoke()")
    public Promise auto() {
        this.fireNextAuto = true;
        return this;
    }

    //当前Promise按手动调用下一个Promise
    @CheckResult(suggest = "invoke()")
    public Promise manual() {
        this.fireNextAuto = false;
        return this;
    }

    //在满足条件的情况下, 插入当前一个链式节点(在当前之后)
    @CheckResult(suggest = "invoke()")
    public Promise insertIf(boolean doWhen, IAction1<Promise> action) {
        if (doWhen) {
            return then(action);
        } else {
            return this;
        }
    }

    //Insert the next Promise when condition is false
    @CheckResult(suggest = "invoke()")
    public Promise insertIfNot(boolean notDoThen, IAction1<Promise> action) {
        if (notDoThen) {
            return this;
        } else {
            return then(action);
        }
    }

    //Set the next Promise
    @CheckResult(suggest = "invoke()")
    public Promise then(@NonNull IAction1<Promise> action) {
        Promise next = new Promise();
        next.action = action;
        next.previous = this;
        this.next = next;
        return next;
    }

    //拦截全局中断事件
    @CheckResult(suggest = "invoke()")
    public Promise ifGlobalInterrupt(@NonNull IAction1<InterruptData> action) {
        this.globalInterrupted = action;
        return this;
    }

    //拦截当前Promise中断事件
    @CheckResult(suggest = "invoke()")
    public Promise ifCurrentInterrupt(@NonNull IAction1<InterruptData> action) {
        this.currentInterrupted = action;
        return this;
    }

    //插入一个Promise在当前之后
    public void insertThen(@NonNull IAction1<Promise> action) {
        Promise currentNext = this.next;
        Promise newNext = new Promise();
        newNext.action = action;
        this.next = newNext;
        if (currentNext != null) {
            newNext.next = currentNext;
        }
    }

    //实现Promise
    public void invoke() {
        findHeader(this).invokeImpl();
    }

    //Set Promise completed and invoke next one
    public void complete() {
        complete(null);
    }

    //以一个结果来完成当前Promise
    public void complete(@Nullable Object result) {
        if (this.isInterrupted) {
            throw new UnsupportedOperationException("Cannot complete a promise when it is interrupted.");
        } else {
            this.result = result;
            if (this.next != null && !this.next.isCancelled) {
                this.next.invokeImpl();
            }
        }
    }

    //Interrupt all Promise
    public void interrupt() {
        interrupt(null, true);
    }

    public void interruptCurrent() {
        interrupt(null, false);
    }

    //Interrupt all if broadcast
    //Interrupt current if do not broadcast
    public void interrupt(boolean broadcast) {
        interrupt(null, broadcast);
    }

    public void interrupt(Object result, boolean broadcast) {
        InterruptData data = new InterruptData(result, broadcast);
        if (broadcast) {
            findHeader(this).globalInterruptImpl(data);
        } else {
            interruptImpl(data);
        }
    }

    private void interruptImpl(InterruptData data) {
        this.isInterrupted = true;
        if (this.currentInterrupted != null) {
            currentInterrupted.invoke(data);
        }
    }

    private void globalInterruptImpl(InterruptData data) {
        this.isInterrupted = true;
        if (this.globalInterrupted != null) {
            globalInterrupted.invoke(data);
        }
        if (data.isBroadcastNeed() && this.next != null) {
            this.next.globalInterruptImpl(data);
        }
    }

    private void invokeImpl() {
        this.isInterrupted = false;
        this.result = null;
        if (!this.isCancelled) {
            if (this.action != null) {
                action.invoke(this);
            }
            if (this.fireNextAuto) {
                complete();
            }
        }
    }

    public void cancel() {
        this.isCancelled = true;
    }

    public void cancelAll() {
        cancel();
        Promise current = findHeader(this);
        while (current != null) {
            current.cancel();
            current = current.next;
        }
    }


    //Try to get result of Promise
    public static Object tryGetResult(@Nullable Promise promise) {
        if (promise != null) {
            return promise.result();
        }
        return null;
    }

    //Find the header of current chain
    private static Promise findHeader(@NonNull Promise from) {
        Promise current = from;
        while (current.previous != null) {
            current = current.previous;
        }
        return current;
    }

    //Get a empty Promise
    @CheckResult(suggest = "invoke()")
    public static Promise empty() {
        return new Promise().auto();
    }

    //Get a leader Promise
    @CheckResult(suggest = "invoke()")
    public static Promise first(@NonNull IAction1<Promise> action) {
        Promise promise = new Promise();
        promise.action = action;
        return promise;
    }

    public static void invoke(@NonNull IAction1<Promise> action) {
        Promise promise = new Promise();
        promise.action = action;
        promise.invoke();
    }

    public static void invoke(@NonNull Promise promise) {
        promise.invoke();
    }

}