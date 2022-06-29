package com.zjj.rxwebsocket;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * name：zjj
 * date：2022/6/14
 * desc：配置webSocket 参数
 */
public class RxWebSocketConfig {

    // 是否打印log
    boolean isPrintLog;

    String logTag ="zjj_websocket";

    OkHttpClient mClient;

    SSLSocketFactory mSslSocketFactory;
    X509TrustManager mTrustManager;

    // 重连间隔时间
    long mReconnectInterval;

    // 重连间隔时间的单位
    TimeUnit mReconnectIntervalTimeUnit;

    public RxWebSocketConfig() {
    }

    public RxWebSocketConfig isPrintLog(boolean isPrintLog) {
        this.isPrintLog = isPrintLog;
        return this;
    }

    public RxWebSocketConfig setLogTag(String logTag) {
        this.logTag = logTag;
        return this;
    }

    public RxWebSocketConfig client(OkHttpClient client) {
        mClient = client;
        return this;
    }

    public RxWebSocketConfig sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        mSslSocketFactory = sslSocketFactory;
        mTrustManager = trustManager;
        return this;
    }

    public RxWebSocketConfig reconnectInterval(long reconnectInterval, TimeUnit reconnectIntervalTimeUnit) {
        this.mReconnectInterval = reconnectInterval;
        this.mReconnectIntervalTimeUnit = reconnectIntervalTimeUnit;
        return this;
    }

    public RxWebSocket build() {
        RxWebSocket instance = RxWebSocket.getInstance();
        instance.setConfig(this);
        return instance;
    }

}
