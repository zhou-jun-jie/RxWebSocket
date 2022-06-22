package com.maxvision.rxwebsocket;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * name：zjj
 * date：2022/6/20
 * desc：类备注
 */
public class BaseWebSocketSubscriber implements Observer<WebSocketInfo> {

    private boolean hasOpened;

    private Disposable disposable;

    @Override
    public final void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public final void onNext(WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isConnect()) {
            hasOpened = true;
            onOpen(webSocketInfo.getWebSocket());
        } else if (webSocketInfo.getString() != null) {
            onMessage(webSocketInfo.getString());
        } else if (webSocketInfo.getByteString() != null) {
            onMessage(webSocketInfo.getByteString());
        } else if (webSocketInfo.isReconnect()) {
            onReconnect();
        } else if (webSocketInfo.getErrorThrowable() != null) {
            onError(webSocketInfo.getErrorThrowable());
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public final void onComplete() {
        if (hasOpened) {
            onClose();
        }
    }

    public final void dispose() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    // 回调--连接成功
    protected void onOpen(WebSocket webSocket) {

    }

    // 回调--重连
    protected void onReconnect() {

    }

    // 回调--消息
    protected void onMessage(String string) {

    }

    // 回调--byteString
    protected void onMessage(ByteString byteString) {

    }

    // 回调--关闭
    protected void onClose() {

    }
}
