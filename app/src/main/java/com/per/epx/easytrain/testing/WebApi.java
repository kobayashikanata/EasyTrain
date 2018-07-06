package com.per.epx.easytrain.testing;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.App;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.interfaces.api.IRemoteApi;
import com.per.epx.easytrain.models.DepotsModel;
import com.per.epx.easytrain.models.req.DepotTimetableAsk;
import com.per.epx.easytrain.models.req.DepotTimetableReply;
import com.per.epx.easytrain.models.req.LineBaseReply;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.models.req.LineDetailReply;
import com.per.epx.easytrain.models.req.SolutionAsk;
import com.per.epx.easytrain.models.req.SolutionReply;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WebApi implements IRemoteApi {
    private static final String TAG = "WebApi";

    private static IRemoteApi remoteApi = null;
    public static IRemoteApi getInstance(){
        synchronized (WebApi.class){
            if(remoteApi == null){
                remoteApi = new WebApi();
            }
            return remoteApi;
        }
    }


    private final OkHttpClient client;
    private final SparseArray<List<WeakReference<Interceptor>>> interceptorRefArray = new SparseArray<>();

    public WebApi(){
        client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build();
    }


    @Override
    public boolean register(int key, Interceptor interceptor) {
        List<WeakReference<Interceptor>> list = interceptorRefArray.get(key, null);
        if(list == null){
            list = new ArrayList<>();
        }
        boolean exist = false;
        for(int i = list.size() - 1; i >= 0; i--){
            WeakReference<Interceptor> ref = list.get(i);
            if(ref == null){
                list.remove(i);
            }else if(ref.get() == null){
                list.remove(i);
            }else if(ref.get() == interceptor){
                exist = true;
            }
        }
        if(!exist){
            list.add(new WeakReference<>(interceptor));
            interceptorRefArray.put(key, list);
            Log.i("REGISTER", "Insert key: " + key);
            return true;
        }
        return false;
    }

    @Override
    public void unregister(int key, Interceptor interceptor) {
        List<WeakReference<Interceptor>> list = interceptorRefArray.get(key, null);
        if(list == null){
            return;
        }
        for(int i = list.size() - 1; i >= 0; i--){
            WeakReference<Interceptor> ref = list.get(i);
            if(ref == null || ref.get() == null || ref.get() == interceptor){
                list.remove(i);
            }
        }
    }

    @Override
    public void unregister(Interceptor interceptor) {
        for(int i = interceptorRefArray.size() - 1; i >= 0; i--){
            List<WeakReference<Interceptor>> list = interceptorRefArray.valueAt(i);
            if(list == null){
                interceptorRefArray.removeAt(i);
            }else{
                for(int j = list.size() - 1; j >= 0; j--){
                    WeakReference<Interceptor> ref = list.get(j);
                    if(ref == null || ref.get() == null || ref.get() == interceptor){
                        list.remove(j);
                    }
                }
            }
        }
    }

    @Override
    public void cleanUp() {
        interceptorRefArray.clear();
    }

    @Override
    public void findTimetable(int key, DepotTimetableAsk toAsk, final IAction1<DepotTimetableReply> onReply) {
        String json = new Gson().toJson(toAsk);
        Log.i("POST", json);
        post(key, App.getServerUrl()+"/search/timetable", RequestBody.create(MEDIA_TYPE_JSON, json),
                onReply, DepotTimetableReply.class);
    }

    @Override
    public void findRoute(int key, SolutionAsk toAsk, final IAction1<SolutionReply> onReply) {
        try{
            String json = new Gson().toJson(toAsk);
            Log.i("POST", json);
            post(key, App.getServerUrl()+"/solutions",
                    RequestBody.create(MEDIA_TYPE_JSON, json),
                    onReply, SolutionReply.class);
        }catch (Exception e) {
            e.printStackTrace();
            callOnExceptionOccupied(interceptorRefArray, key, null, null, e);
        }
    }

    @Override
    public void findLine(int key, LineDetailAsk toAsk, IAction1<LineDetailReply> onReply) {
        String json = new Gson().toJson(toAsk);
        Log.i("POST", json);
        post(key, App.getServerUrl()+"/line/detail",
                RequestBody.create(MEDIA_TYPE_JSON, json),
                onReply, LineDetailReply.class);
    }

    @Override
    public void findLinesOfKeyword(int key, String keyword, IAction1<List<LineBaseReply>> onReply) {
        try{
            RequestBody body = new FormBody.Builder()
                    .add("keyword", keyword)
                    .build();
            Log.i("POST", keyword);
            postForType(key, App.getServerUrl()+"/lines", body, onReply, new TypeToken<List<LineBaseReply>>(){}.getType());
        }catch (Exception e) {
            e.printStackTrace();
            callOnExceptionOccupied(interceptorRefArray, key, null, null, e);
        }
    }

    @Override
    public void findDepotsModel(int key, IAction1<DepotsModel> onReply) {
        try{
            Request request = new Request.Builder().url(App.getServerUrl()+"/depots")
                    .get()
                    .build();
            requestImpl(key, request, onReply, DepotsModel.class, true);
        }catch (Exception e) {
            e.printStackTrace();
            callOnExceptionOccupied(interceptorRefArray, key, null, null, e);
        }
    }

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private <Reply> void post(int key, String url, RequestBody requestBody, final IAction1<Reply> onReply, Class<Reply> replyClass){
        try{
            Request request = new Request.Builder().url(url)
                    .post(requestBody)
                    .build();
            requestImpl(key, request, onReply, replyClass);
        }catch (Exception e){
            e.printStackTrace();
            callOnExceptionOccupied(interceptorRefArray, key, null, null, e);
        }
    }

    private <Reply> void postForType(int key, String url, RequestBody requestBody, final IAction1<Reply> onReply, Type resultType){
        try{
            Request request = new Request.Builder().url(url)
                .post(requestBody)
                .build();
            requestImplForType(key, request, onReply, resultType);
        }catch (Exception e){
            e.printStackTrace();
            callOnExceptionOccupied(interceptorRefArray, key, null, null, e);
        }
    }

    private <Reply> void requestImpl(final int key, Request request, final IAction1<Reply> onReply, final Class<Reply> replyClass){
        requestImpl(key, request, onReply, replyClass, false);
    }

    private <Reply> void requestImpl(final int key, Request request, final IAction1<Reply> onReply, final Class<Reply> replyClass,
                                     final boolean invokeEvenError){
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if(invokeEvenError){
                    try {
                        onReply.invoke(null);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                callOnFailure(interceptorRefArray, key, call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                Reply reply = null;
                try{
                    String json = body != null ? body.string() : null;
                    Log.i(TAG, "BackCode:"+response.code());
                    Log.i(TAG, "Received:"+json);
                    reply = new Gson().fromJson(json, replyClass);
                    if(reply == null){
                        throw new NullPointerException("Receive data with wrong format.");
                    }
                    onReply.invoke(reply);
                }catch (Exception e){
                    e.printStackTrace();
                    if(invokeEvenError){
                        try {
                            onReply.invoke(null);
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                    callOnExceptionOccupied(interceptorRefArray, key, call, response, e);
                }
            }
        });
    }

    private <Reply> void requestImplForType(final int key, Request request, final IAction1<Reply> onReply, final Type resultType){
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callOnFailure(interceptorRefArray, key, call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                Reply reply = null;
                try{
                    String json = body != null ? body.string() : null;
                    Log.i(TAG, "BackCode:"+response.code());
                    Log.i(TAG, "Received:"+json);
                    reply = new Gson().fromJson(json, resultType);
                    if(reply == null){
                        throw new NullPointerException("Receive data with wrong format.");
                    }
                    onReply.invoke(reply);
                } catch (Exception e){
                    e.printStackTrace();
                    callOnExceptionOccupied(interceptorRefArray, key, call, response, e);
                }
            }
        });
    }

    private void callOnFailure(SparseArray<List<WeakReference<Interceptor>>> interceptorRefArray, int key,
                               @NonNull Call call, @NonNull IOException e){
        List<WeakReference<Interceptor>> refList = interceptorRefArray.get(key, null);
        if(refList != null && refList.size() > 0){
            for(int i = refList.size() - 1; i >= 0; i--){
                WeakReference<Interceptor> ref = refList.get(i);
                if(ref == null || ref.get() == null){
                    refList.remove(i);
                }else{
                    ref.get().onFailure(call, e);
                }
            }
        }
    }

    private void callOnExceptionOccupied(SparseArray<List<WeakReference<Interceptor>>> interceptorRefArray, int key,
                                         Call call,
                                         Response response,
                                         Exception e){
        List<WeakReference<Interceptor>> refList = interceptorRefArray.get(key, null);
        if(refList != null && refList.size() > 0){
            for(int i = refList.size() - 1; i >= 0; i--){
                WeakReference<Interceptor> ref = refList.get(i);
                if(ref == null || ref.get() == null){
                    refList.remove(i);
                }else{
                    ref.get().onExceptionOccupied(call, response, e);
                }
            }
        }
    }

}
