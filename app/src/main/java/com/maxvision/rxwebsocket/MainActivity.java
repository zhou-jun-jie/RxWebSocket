package com.maxvision.rxwebsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.WebSocket;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "zjj_test";

    // url地址
    private EditText etUrl;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = findViewById(R.id.et_url);
        url = "ws://"+etUrl.getText().toString()+":12345";
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
        WebSocketManager.getInstance().getRxWebSocket().send(url, "测试发送消息1")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "发送消息成功:" + aBoolean);
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void close(View view) {
        WebSocketManager.getInstance().getRxWebSocket().close(url)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "关闭连接:" + aBoolean);
                    }
                });
    }

    public void closeNow(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeNow(url);
        Log.e(TAG, "关闭连接 closeNow");
    }

    @SuppressLint("CheckResult")
    public void closeAll(View view) {
        WebSocketManager.getInstance().getRxWebSocket().closeAll()
                .subscribe(new Consumer<List<Boolean>>() {
                    @Override
                    public void accept(List<Boolean> booleans) throws Exception {
                        Log.e(TAG, "关闭All:" + booleans.size());
                    }
                });
    }

}