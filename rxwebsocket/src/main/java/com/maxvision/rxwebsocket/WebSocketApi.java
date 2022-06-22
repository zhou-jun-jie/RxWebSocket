package com.maxvision.rxwebsocket;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okio.ByteString;

/**
 * name：zjj
 * date：2022/6/14
 * desc：api调用接口
 */
public interface WebSocketApi {

    // 获取连接,返回观察对象,默认为5秒
    Observable<WebSocketInfo> get(String url);

    // 设置超时时间,指定时间尝试重连
    Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit);

    // 同步发送消息String
    Observable<Boolean> send(String url,String msg);

    // 同步发送消息ByteString
    Observable<Boolean> send(String url, ByteString byteString);

    // 异步发送消息String
    Observable<Boolean> asyncSend(String url,String msg);

    // 异步发送消息
    Observable<Boolean> asyncSend(String url,ByteString byteString);

    // 关闭指定url的连接
    Observable<Boolean> close(String url);

    boolean closeNow(String url);

    // 关闭当前所有连接
    Observable<List<Boolean>> closeAll();

    void closeAllNow();

}
