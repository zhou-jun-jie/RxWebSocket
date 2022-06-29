package com.zjj.rxwebsocket;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * name：zjj
 * date：2022/6/20
 * desc：类备注
 */
public class WebSocketManager {

    private RxWebSocket rxWebSocket;
    private static final int CONNECT_TIME_OUT = 30;
    private static final int WRITE_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    private static final int PING_INTERVAL = 5;
    // 重连时间
    private static final int RECONNECT_INTERVAL = 5;
    // 收到消息的超时时间
    private static final int TIME_INTERVAL = 5;

    private WebSocketManager() {
        init();
    }

    private static class WebSocketHolder {
        private static final WebSocketManager INSTANCE = new WebSocketManager();
    }

    public static WebSocketManager getInstance() {
        return WebSocketHolder.INSTANCE;
    }

    private void init() {
        rxWebSocket = new RxWebSocketConfig()
                .isPrintLog(true)
                .client(getOkHttpClient())
                .reconnectInterval(RECONNECT_INTERVAL, TimeUnit.SECONDS)
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .pingInterval(PING_INTERVAL, TimeUnit.SECONDS)
                .build();
    }

    public final RxWebSocket getRxWebSocket() {
        return rxWebSocket;
    }

    public void registerWebSocketMsg(String url, WebSocketSubscriber<?> webSocketSubscriber) {
        registerWebSocketMsg(url, TIME_INTERVAL, TimeUnit.SECONDS, webSocketSubscriber);
    }

    public void registerWebSocketMsg(String url, long timeout, TimeUnit timeUnit, WebSocketSubscriber<?> webSocketSubscriber) {
        rxWebSocket.get(url, timeout, timeUnit)
                .subscribe(webSocketSubscriber);
    }

}
