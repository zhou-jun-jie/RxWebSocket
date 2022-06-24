package com.maxvision.rxwebsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import okhttp3.WebSocket;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "zjj_test";

    private static final String TAG2 = "zjj_test2";

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // url地址
        EditText etUrl = findViewById(R.id.et_url);
        url = "ws://"+ etUrl.getText().toString()+":12345";
    }

    public void connectOne(View view) {
        WebSocketManager.getInstance().registerWebSocketMsg(url, new WebSocketSubscriber<Object>() {
            @Override
            protected void onMessage(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.e(TAG, "onError:" + e.getMessage());
            }

            @Override
            protected void onOpen(WebSocket webSocket) {
                super.onOpen(webSocket);
                Log.e(TAG, "onOpen:" + url);
            }

            @Override
            protected void onReconnect() {
                super.onReconnect();
                Log.e(TAG, "onReconnect");
            }

            @Override
            protected void onMessage(ByteString byteString) {
                super.onMessage(byteString);
                Log.e(TAG, "onMessage:" + byteString);
            }

            @Override
            protected void onClose() {
                super.onClose();
                Log.e(TAG, "onClose");
            }


        });
    }

    @SuppressLint("CheckResult")
    public void sendOne(View view) {
        WebSocketManager.getInstance().getRxWebSocket().sendRx(url, "测试发送消息1")
                .subscribe(aBoolean -> Log.e(TAG, "发送消息成功:" + aBoolean));
    }

    @SuppressLint("CheckResult")
    public void close(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeRx(url)
                .subscribe(aBoolean -> Log.e(TAG, "关闭连接1:" + aBoolean));
    }

    public void closeNow(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeNow(url);
        Log.e(TAG, "关闭连接1 closeNow");
    }

    @SuppressLint("CheckResult")
    public void closeAll(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeAllRx()
                .subscribe(booleans -> Log.e(TAG, "关闭All:" + booleans.size()));
    }

    private String url2 = "ws:192.168.111.222:8181";

    public void connectTwo(View view) {
        WebSocketManager.getInstance().registerWebSocketMsg(url2, new WebSocketSubscriber<Object>() {
            @Override
            protected void onMessage(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.e(TAG2, "onError:" + e.getMessage());
            }

            @Override
            protected void onOpen(WebSocket webSocket) {
                super.onOpen(webSocket);
                Log.e(TAG2, "onOpen:" + url);
            }

            @Override
            protected void onReconnect() {
                super.onReconnect();
                Log.e(TAG2, "onReconnect");
            }

            @Override
            protected void onMessage(ByteString byteString) {
                super.onMessage(byteString);
                Log.e(TAG2, "onMessage:" + byteString);
            }

            @Override
            protected void onClose() {
                super.onClose();
                Log.e(TAG2, "onClose");
            }

        });
    }

    @SuppressLint("CheckResult")
    public void sendTwo(View view) {
        WebSocketManager.getInstance().getRxWebSocket().sendRx(url2,"测试消息2")
        .subscribe(aBoolean -> Log.e(TAG2,"发送消息:"+aBoolean));
    }

    @SuppressLint("CheckResult")
    public void close2(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeRx(url2)
                .subscribe(aBoolean -> Log.e(TAG2, "关闭连接2:" + aBoolean));
    }

    public void closeNow2(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeNow(url2);
        Log.e(TAG2, "关闭连接2 closeNow");
    }

    @SuppressLint("CheckResult")
    public void send(View view) {
        WebSocketManager.getInstance().getRxWebSocket().sendRx("测试发送消息__Hello")
                .subscribe(aBoolean -> Log.e(TAG,"发送消息__唯一:"+aBoolean))
        ;
    }
}