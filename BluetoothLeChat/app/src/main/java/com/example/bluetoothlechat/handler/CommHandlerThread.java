package com.example.bluetoothlechat.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.example.bluetoothlechat.bluetooth.ChatServer;

public class CommHandlerThread extends HandlerThread {
    public static final int SEND_MESSAGE = 10001;
    Handler handler;
    ChatServer chatServer;
    public CommHandlerThread(String name, ChatServer chatServer) {
        super(name);
        this.chatServer = chatServer;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SEND_MESSAGE:{
                        //sendPacket((byte[])msg.obj);
                    }
                }

            }
        };
    }
}
