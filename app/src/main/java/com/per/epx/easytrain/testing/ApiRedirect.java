package com.per.epx.easytrain.testing;

import android.support.annotation.NonNull;
import android.util.Log;

import com.per.epx.easytrain.helpers.ActionHelper;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.interfaces.api.IRemoteApi;
import com.per.epx.easytrain.models.DepotsModel;
import com.per.epx.easytrain.viewmodels.DepotsContactViewModel;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ApiRedirect {
    private static boolean mUsingLocal = true;
    private static DepotsContactViewModel depotsContact;
    private static ActionsInvoker<DepotsContactViewModel> contactInvoker = new ActionsInvoker<>();
    private static boolean isCalling = false;

    public static IRemoteApi getApi(){
        return mUsingLocal ? LocalApiTestImpl.getInstance():
                WebApi.getInstance();
    }

    public static void usingLocal(boolean usingLocal){
        getApi().cleanUp();
        mUsingLocal = usingLocal;
        depotsContact = null;
    }

    public static void resetDepotsContact(){
        depotsContact = null;
    }

    public static void getDepotsContact(int key, final IAction1<DepotsContactViewModel> onContact){
        if(depotsContact == null){
            synchronized (ApiRedirect.class){
                if(depotsContact == null){
                    contactInvoker.addAction(onContact);
                    if(!isCalling){
                        isCalling = true;
                        final Interceptor interceptor = new Interceptor() {
                            @Override
                            public void onExceptionOccupied(@NonNull Call call, @NonNull Response response, @NonNull Exception e) {
                                isCalling = false;
                            }

                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                isCalling = false;
                            }
                        };
                        getApi().register(key, interceptor);
                        getApi().findDepotsModel(key, new IAction1<DepotsModel>() {
                            @Override
                            public void invoke(DepotsModel data) {
                                Log.d("ApiRedirect", "Convert depot model begin");
                                getApi().unregister(interceptor);
                                try {
                                    if(data != null){
                                        depotsContact = new DepotsContactViewModel(data);
                                        contactInvoker.invoke(depotsContact);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                contactInvoker.clear();
                                Log.d("ApiRedirect", "Convert depot model end");
                                isCalling = false;
                            }
                        });
                    }
                }else{
                    ActionHelper.safelyInvoke(onContact, depotsContact);
                }
            }
        }else{
            ActionHelper.safelyInvoke(onContact, depotsContact);
        }
    }

    private static class ActionsInvoker<T>{
        private List<IAction1<T>> refList = new ArrayList<>();

        public void addAction(IAction1<T> action){
            Log.d("WeakActionsInvoker", "Add action "+action);
            refList.add(action);
        }

        public void clear(){
            refList.clear();
            refList = new ArrayList<>();
        }

        public void invoke(T data){
            for (int i = refList.size() - 1; i >= 0; i--){
                IAction1<T> refOne = refList.get(i);
                if(refOne == null){
                    Log.d("WeakActionsInvoker", "Remove action "+i);
                    refList.remove(i);
                }else{
                    Log.d("WeakActionsInvoker", "Invoke action "+refOne);
                    ActionHelper.safelyInvoke(refOne, data);
                }
            }
        }
    }
}
