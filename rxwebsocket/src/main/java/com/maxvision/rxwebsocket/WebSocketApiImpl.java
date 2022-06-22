package com.maxvision.rxwebsocket;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * name：zjj
 * date：2022/6/14
 * desc：具体实现类
 */
public class WebSocketApiImpl implements WebSocketApi {

    // 是否打印log
    boolean isPrintLog;

    String logTag;

    SSLSocketFactory mSslSocketFactory;
    X509TrustManager mTrustManager;

    OkHttpClient mClient;

    // 重连间隔时间
    long mReconnectInterval;

    // 重连间隔时间的单位
    TimeUnit mReconnectIntervalTimeUnit;

    // 缓存观察者对象，Url对应一个Observable
    private final Map<String, Observable<WebSocketInfo>> mObservableMap;

    // 缓存url和对应的WebSocket实例,同一个url共享一个WebSocket连接
    private final Map<String, WebSocket> mWebSocketMap;

    private final Map<String, Disposable> mDisposableMap;

    public WebSocketApiImpl(boolean isPrintLog, String logTag, OkHttpClient client, SSLSocketFactory sslSocketFactory,
                            X509TrustManager trustManager, long reconnectInterval,
                            TimeUnit reconnectIntervalTimeUnit) {
        this.isPrintLog = isPrintLog;
        this.logTag = logTag;
        mClient = client;
        mSslSocketFactory = sslSocketFactory;
        mTrustManager = trustManager;
        mReconnectInterval = reconnectInterval;
        mReconnectIntervalTimeUnit = reconnectIntervalTimeUnit;
        mObservableMap = new ConcurrentHashMap<>();
        mWebSocketMap = new ConcurrentHashMap<>();
        mDisposableMap = new ConcurrentHashMap<>();
    }

    @Override
    public Observable<WebSocketInfo> get(String url) {
        return getWebSocketInfo(url);
    }

    @Override
    public Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit) {
        return getWebSocketInfo(url, timeout, timeUnit);
    }

    @Override
    public Observable<Boolean> send(String url, String msg) {
        return Observable.create(emitter -> {
            WebSocket webSocket = mWebSocketMap.get(url);
            if (null == webSocket) {
                emitter.onError(new NullPointerException("The WebSocket is Null,please check"));
            } else {
                emitter.onNext(webSocket.send(msg));
            }
        });
    }

    @Override
    public Observable<Boolean> send(String url, ByteString byteString) {
        return Observable.create(emitter -> {
            WebSocket webSocket = mWebSocketMap.get(url);
            if (null == webSocket) {
                emitter.onError(new NullPointerException("The WebSocket is Null,please check"));
            } else {
                emitter.onNext(webSocket.send(byteString));
            }
        });
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
        WebSocket webSocket = mWebSocketMap.get(url);
        if (null == webSocket) {
            // TODO
            Disposable disposable = mDisposableMap.get(url);
            if (null != disposable) {
                disposable.dispose();
                mDisposableMap.remove(url);
                mObservableMap.remove(url);
            }
            return Observable.create(emitter -> emitter.onNext(true));
        } else {
            return Observable.create((ObservableOnSubscribe<WebSocket>)
                    emitter -> emitter.onNext(webSocket))
                    .map(this::closeWebSocket);
        }
    }

    @Override
    public boolean closeNow(String url) {
        // TODO 判断
        WebSocket webSocket = mWebSocketMap.get(url);
        if (null == webSocket) {
            Disposable disposable = mDisposableMap.get(url);
            if (null != disposable) {
                disposable.dispose();
                mDisposableMap.remove(url);
                mObservableMap.remove(url);
            }
            return true;
        } else {
            return closeWebSocket(webSocket);
        }
    }

    @Override
    public Observable<List<Boolean>> closeAll() {
        if (mWebSocketMap.size() == 0) {
            return Observable.just(mDisposableMap)
                    .map(Map::values)
                    .concatMap((Function<Collection<Disposable>, ObservableSource<Disposable>>) disposables -> Observable.fromIterable(disposables))
                    .map(disposable -> {
                        disposable.dispose();
                        return true;
                    }).collect((Callable<List<Boolean>>) ArrayList::new, List::add)
                    .toObservable();
        } else {
            return Observable.just(mWebSocketMap)
                    .map(Map::values)
                    .concatMap((Function<Collection<WebSocket>, ObservableSource<WebSocket>>)
                            Observable::fromIterable)
                    .map(this::closeWebSocket)
                    .collect((Callable<List<Boolean>>) ArrayList::new, List::add).toObservable();
        }
    }

    @Override
    public void closeAllNow() {
        for (Map.Entry<String, WebSocket> entry : mWebSocketMap.entrySet()) {
            closeNow(entry.getKey());
        }
    }

    // 关闭WebSocket
    private boolean closeWebSocket(WebSocket webSocket) {
        boolean result = webSocket.close(WebSocketConstants.CLOSE_USER, null);
        if (result) {
            removeAllMap(webSocket);
        }
        return result;
    }

    public Observable<WebSocketInfo> getWebSocketInfo(String url) {
        return getWebSocketInfo(url, 5, TimeUnit.SECONDS);
    }


    public Observable<WebSocketInfo> getWebSocketInfo(final String url, final long timeout,
                                                      final TimeUnit timeUnit) {
        // 缓存中读取
        Observable<WebSocketInfo> observable = mObservableMap.get(url);
        if (null == observable) {
            observable = Observable.create(new WebSocketOnSubscribe(url))
                    .timeout(timeout, timeUnit)
                    .retry(throwable -> {
                        if (isPrintLog) {
                            Log.e(logTag, "重试:" + throwable.getMessage());
                        }
                        return throwable instanceof IOException || throwable instanceof TimeoutException;
                    })
                    .doOnDispose(() -> {
                        mObservableMap.remove(url);
                        mWebSocketMap.remove(url);
                        if (isPrintLog) {
                            Log.e(logTag, "OnDispose");
                        }
                    })
                    .doOnSubscribe(disposable -> {
                        if (isPrintLog) {
                            Log.e(logTag, "doOnSubscribe");
                        }
                        mDisposableMap.put(url,disposable);
                    })
                    .doOnNext(webSocketInfo -> {
                        if (webSocketInfo.isConnect()) {
                            mWebSocketMap.put(url, webSocketInfo.getWebSocket());
                        }
                        if (isPrintLog) {
                            Log.e(logTag, "doOnNext");
                        }
                    })
                    .share()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            mObservableMap.put(url, observable);
        } else {
            WebSocket webSocket = mWebSocketMap.get(url);
            if (null != webSocket) {
                observable = observable.startWith(new WebSocketInfo(webSocket, true))
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
        return observable;
    }

    // 组装数据源
    private final class WebSocketOnSubscribe implements ObservableOnSubscribe<WebSocketInfo> {

        private final String mWebSocketUrl;
        private WebSocket mWebSocket;

        public WebSocketOnSubscribe(String webSocketUrl) {
            mWebSocketUrl = webSocketUrl;
        }

        @Override
        public void subscribe(ObservableEmitter<WebSocketInfo> emitter) {
            initWebSocket(emitter);
            Log.e(logTag, "是否为空:" + (mWebSocket == null)+",thread:"+Thread.currentThread().getName());
            if (mWebSocket != null) {
                //降低重连频率
                if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                    long millis = mReconnectIntervalTimeUnit.toMillis(mReconnectInterval);
                    if (millis == 0) {
                        millis = 1000;
                    }
                    SystemClock.sleep(millis);
                    WebSocketInfo webSocketInfo = new WebSocketInfo(mWebSocket);
                    webSocketInfo.setReconnect(true);
                    emitter.onNext(webSocketInfo);
                }
            }
        }

        private Request createRequest(String url) {
            return new Request.Builder().get().url(url).build();
        }

        private synchronized void initWebSocket(ObservableEmitter<WebSocketInfo> emitter) {
            mWebSocket = mClient.newWebSocket(createRequest(mWebSocketUrl), new WebSocketListener() {
                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                    super.onOpen(webSocket, response);
                    if (isPrintLog) {
                        Log.e(logTag, "onOpen");
                    }
                    // 连接成功
                    mWebSocketMap.put(mWebSocketUrl, webSocket);
                    if (!emitter.isDisposed()) {
                        // 连接成功
                        emitter.onNext(new WebSocketInfo(webSocket, true));
                    }
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                    super.onMessage(webSocket, text);
                    if (isPrintLog) {
                        Log.e(logTag, "收到消息string:" + text);
                    }
                    // 收到消息
                    if (!emitter.isDisposed()) {
                        emitter.onNext(new WebSocketInfo(webSocket, text));
                    }
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                    if (isPrintLog) {
                        Log.e(logTag, "收到消息bytes:" + bytes);
                    }
                    // 收到消息
                    if (!emitter.isDisposed()) {
                        emitter.onNext(new WebSocketInfo(webSocket, bytes));
                    }
                }

                @Override
                public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosing(webSocket, code, reason);
                    if (isPrintLog) {
                        Log.e(logTag, "onClosing:" + code);
                    }
                }

                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosed(webSocket, code, reason);
                    if (isPrintLog) {
                        Log.e(logTag, "onClosed:" + code);
                    }
                    if (WebSocketConstants.CLOSE_USER == code) {
                        // 主动断开
                        emitter.onComplete();
                    }
                }

                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    if (isPrintLog) {
                        Log.e(logTag, "onFailure:" + t.getMessage());
                    }
                    removeWebSocketObservable(webSocket);
                    if (!emitter.isDisposed()) {
                        /* 目前查找原因onError未调用,用onNext发送
                        emitter.onError(t);*/
                        emitter.onNext(new WebSocketInfo(t));
                    }
                }
            });

            emitter.setCancellable(() -> {
                if (null != mWebSocket) {
                    if (isPrintLog) {
                        Log.e(logTag, "Close WebSocket");
                    }
                    mWebSocket.close(WebSocketConstants.CLOSE_NO_MESSAGE, "Close WebSocket");
                }
                if (isPrintLog) {
                    Log.e(logTag, mWebSocketUrl + " --> cancel ");
                }
            });
        }
    }

    private void removeAllMap(WebSocket webSocket) {
        for (Map.Entry<String, WebSocket> entry : mWebSocketMap.entrySet()) {
            if (entry.getValue() == webSocket) {
                String url = entry.getKey();
                mWebSocketMap.remove(url);
                mObservableMap.remove(url);
                mDisposableMap.remove(url);
            }
        }
    }

    private void removeWebSocketObservable(WebSocket webSocket) {
        for (Map.Entry<String, WebSocket> entry : mWebSocketMap.entrySet()) {
            if (entry.getValue() == webSocket) {
                String url = entry.getKey();
                mWebSocketMap.remove(url);
                mObservableMap.remove(url);
            }
        }
    }



}
