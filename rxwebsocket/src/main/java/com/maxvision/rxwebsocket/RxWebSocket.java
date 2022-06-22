package com.maxvision.rxwebsocket;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okio.ByteString;

/**
 * name：zjj
 * date：2022/6/14
 * desc：类备注
 */
public class RxWebSocket implements WebSocketApi {

    // 是否打印log
    boolean isPrintLog;

    String logTag;

    OkHttpClient mClient;

    SSLSocketFactory mSslSocketFactory;
    X509TrustManager mTrustManager;

    // 重连间隔时间
    long mReconnectInterval;

    // 重连间隔时间的单位
    TimeUnit mReconnectIntervalTimeUnit;

    private WebSocketApi webSocketApi;

    private RxWebSocket() {}

    public static RxWebSocket instance;

    public static RxWebSocket getInstance() {
        if (null == instance) {
            synchronized (RxWebSocket.class) {
                if (null == instance) {
                    instance = new RxWebSocket();
                }
            }
        }
        return instance;
    }

    public void setConfig(RxWebSocketConfig builder) {
        this.isPrintLog = builder.isPrintLog;
        this.logTag = builder.logTag;
        this.mClient = builder.mClient;
        this.mSslSocketFactory = builder.mSslSocketFactory;
        this.mTrustManager = builder.mTrustManager;
        this.mReconnectInterval = builder.mReconnectInterval;
        this.mReconnectIntervalTimeUnit = builder.mReconnectIntervalTimeUnit;
        setUp();
    }

    private void setUp() {
        webSocketApi = new WebSocketApiImpl(isPrintLog, logTag, mClient, mSslSocketFactory, mTrustManager,
                mReconnectInterval, mReconnectIntervalTimeUnit);
    }

    @Override
    public Observable<WebSocketInfo> get(String url) {
        return webSocketApi.get(url);
    }

    @Override
    public Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit) {
        return webSocketApi.get(url,timeout,timeUnit);
    }

    @Override
    public Observable<Boolean> send(String url, String msg) {
        return webSocketApi.send(url, msg);
    }

    @Override
    public Observable<Boolean> send(String url, ByteString byteString) {
        return webSocketApi.send(url, byteString);
    }

    @Override
    public Observable<Boolean> asyncSend(String url, String msg) {
        return null;
    }

    @Override
    public Observable<Boolean> asyncSend(String url, ByteString byteString) {
        return null;
    }

    @Override
    public Observable<Boolean> close(String url) {
        return webSocketApi.close(url);
    }

    @Override
    public boolean closeNow(String url) {
        return webSocketApi.closeNow(url);
    }

    @Override
    public Observable<List<Boolean>> closeAll() {
        return webSocketApi.closeAll();
    }

    @Override
    public void closeAllNow() {
        webSocketApi.closeAllNow();
    }
}
