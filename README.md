# RxWebSocket 使用步骤 请参考项目demo中的WebSocketManager,可以直接使用,里面的参数配置可自行配置

## 1. app中build文件依赖

> implementation 'io.github.zhou-jun-jie:RxWebSocket:1.0.2'

## 2. 初始化OkHttpClient

	return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)   // 连接超时: 自己定义常量
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)         // 读取超时: 自己定义常量
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)       // 写超时: 自己定义常量
                .pingInterval(PING_INTERVAL, TimeUnit.SECONDS)        // ping的连接时间: 自己定义常量
                .build();
                
 ## 3.  初始化RxWebSocket
 
 	rxWebSocket = new RxWebSocketConfig()
                .isPrintLog(true)
                .client(getOkHttpClient())
                .reconnectInterval(RECONNECT_INTERVAL, TimeUnit.SECONDS)
                .build();
               
 ## 4. 注册websocket消息
  
	public void registerWebSocketMsg(String url, long timeout, TimeUnit timeUnit, WebSocketSubscriber<?> webSocketSubscriber) {
        rxWebSocket.get(url, timeout, timeUnit)
                .subscribe(webSocketSubscriber);
    }
 
 ## 5. 最后使用
	WebSocketManager.getInstance().registerWebSocketMsg(url, new WebSocketSubscriber<String>() {

            @Override
            protected void onMessage(String str) {

            }
        });
