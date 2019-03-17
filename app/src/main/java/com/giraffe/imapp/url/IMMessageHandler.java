package com.giraffe.imapp.url;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;

public class IMMessageHandler extends BmobIMMessageHandler {

    @Override
    public void onMessageReceive(final MessageEvent event) {
        super.onMessageReceive(event);
        //在线消息
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        super.onOfflineReceive(event);
        //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
    }
}