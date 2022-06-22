package com.maxvision.rxwebsocket;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * name：zjj
 * date：2022/6/20
 * desc：websocket观察者
 */
public abstract class WebSocketSubscriber<T> extends BaseWebSocketSubscriber {

    private static final Gson GSON = new Gson();

    private Type type;

    public WebSocketSubscriber() {
        analysisType();
    }

    private void analysisType() {
        Type superclass = getClass().getGenericSuperclass();
        if (null == superclass || superclass instanceof Class) {
            throw new RuntimeException("No generics found!");
        }
        ParameterizedType type = (ParameterizedType) superclass;
        this.type = type.getActualTypeArguments()[0];
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onMessage(String string) {
        Observable.just(string)
                .map((Function<String, T>) s -> {
                    try {
                        return GSON.fromJson(s, type);
                    } catch (JsonSyntaxException e) {
                        return GSON.fromJson(GSON.fromJson(s, String.class), type);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMessage);
    }

    protected abstract void onMessage(T t);
}
