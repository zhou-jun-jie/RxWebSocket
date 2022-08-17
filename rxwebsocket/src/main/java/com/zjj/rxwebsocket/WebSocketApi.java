package com.zjj.rxwebsocket;

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

    /**
     * 是否连接
     */
    boolean isConnect();

    /**
     * 是否连接
     * @param url 多个url的情况下需要传入url
     */
    boolean isConnect(String url);

    /**
     * 获取连接,返回观察对象,默认为5秒
     */
    Observable<WebSocketInfo> get(String url);

    /**
     * 设置超时时间,指定时间尝试重连
     */
    Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit);

    /**
     * 同步发送消息String,无需指定Url(只有一个websocket时可以使用该API),注意返回的是Observable,调用需consumer
     */
    Observable<Boolean> sendRx(String msg);

    /**
     * 同步发送消息String,注意返回的是Observable,调用需consumer
     */
    Observable<Boolean> sendRx(String url, String msg);

    /**
     * 同步发送消息ByteString,注意返回的是Observable,调用需consumer
     */
    Observable<Boolean> sendRx(String url, ByteString byteString);

    /**
     * 同步发送消息ByteString,注意返回的是Observable,调用需consumer
     */
    Observable<Boolean> sendRx(ByteString byteString);

    /**
     * 同步返送消息string,无需指定url(只有一个websocket的连接时,使用改api)
     */
    boolean send(String msg);

    /**
     * 同步返送消息byteString,无需指定url(只有一个websocket的连接时,使用改api)
     */
    boolean send(ByteString byteString);

    /**
     * 同步发送消息String
     */
    boolean send(String url,String msg);

    /**
     * 同步发送消息
     */
    boolean send(String url,ByteString byteString);

    /**
     * 关闭指定url的连接,注意返回的是Observable,调用需consumer
     */
    Observable<Boolean> closeRx(String url);

    /**
     * 关闭指定url的网络连接
     */
    boolean closeNow(String url);

    /**
     * 关闭当前所有连接,注意返回的是Observable,调用需consumer
     */
    Observable<List<Boolean>> closeAllRx();

    /**
     * 关闭当前的所有连接
     */
    void closeAllNow();

}
