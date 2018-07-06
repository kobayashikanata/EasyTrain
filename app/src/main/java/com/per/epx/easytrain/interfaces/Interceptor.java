package com.per.epx.easytrain.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface Interceptor {
    void onExceptionOccupied(@NonNull Call call, @NonNull Response response, @NonNull Exception e);
    void onFailure(@NonNull Call call, @NonNull IOException e);
}
