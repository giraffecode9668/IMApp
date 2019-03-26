package com.giraffe.imapp.url;

import android.util.Log;
import android.widget.Toast;

import com.giraffe.imapp.activity.LoginActivity;
import com.giraffe.imapp.activity.MainActivity;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class IMMessageHandler extends BmobIMMessageHandler {


    BmobIMConversation conversation;

    @Override
    public void onMessageReceive(final MessageEvent event) {
        super.onMessageReceive(event);
        //在线消息
        BmobIMMessage msg = event.getMessage();
        Log.d("handle",msg.toString());
        if (msg.getMsgType().equals("add")){//好友请求功能
            Toast.makeText(getApplicationContext(),"有好友申请消息",Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        super.onOfflineReceive(event);
        //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
        Map<String, List<MessageEvent>> map = event.getEventMap();

        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                //处理每条消息
                BmobIMMessage message = list.get(i).getMessage();
                if (message.getMsgType().equals("add")){
                    Toast.makeText(getApplicationContext(),"有好友申请消息",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}