package com.maxvision.rxwebsocket;

import okhttp3.WebSocket;
import okio.ByteString;

/**
 * name：zjj
 * date：2022/6/13
 * desc：webSocket实体类
 */
public class WebSocketInfo {

    private WebSocket mWebSocket;

    private String mString;

    private ByteString mByteString;

    // 连接成功
    private boolean isConnect;

    // 重连
    private boolean isReconnect;

    // 异常信息
    private Throwable errorThrowable;

    public Throwable getErrorThrowable() {
        return errorThrowable;
    }

    public void setErrorThrowable(Throwable errorThrowable) {
        this.errorThrowable = errorThrowable;
    }

    WebSocketInfo(WebSocket webSocket) {
        mWebSocket = webSocket;
    }

    WebSocketInfo(WebSocket webSocket, boolean isConnect) {
        mWebSocket = webSocket;
        this.isConnect = isConnect;
    }

    WebSocketInfo(WebSocket webSocket, String mString) {
        mWebSocket = webSocket;
        this.mString = mString;
    }

    WebSocketInfo(WebSocket webSocket, ByteString byteString) {
        mWebSocket = webSocket;
        mByteString = byteString;
    }

    WebSocketInfo(Throwable error) {
        errorThrowable = error;
    }

    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public void setWebSocket(WebSocket mWebSocket) {
        this.mWebSocket = mWebSocket;
    }

    public String getString() {
        return mString;
    }

    public void setString(String mString) {
        this.mString = mString;
    }

    public ByteString getByteString() {
        return mByteString;
    }

    public void setByteString(ByteString mByteString) {
        this.mByteString = mByteString;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public void setReconnect(boolean reconnect) {
        isReconnect = reconnect;
    }
}
