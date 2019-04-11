package com.giraffe.imapp.url;

import android.util.Log;
import android.widget.Toast;

import com.giraffe.imapp.activity.LoginActivity;
import com.giraffe.imapp.activity.MainActivity;
import com.giraffe.imapp.pojo.User;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class IMMessageHandler extends BmobIMMessageHandler {


    BmobIMConversation conversation;

    @Override
    public void onMessageReceive(final MessageEvent event) {
        super.onMessageReceive(event);
        //在线消息
        BmobIMMessage msg = event.getMessage();
        Log.d("handle", msg.toString());
        if (msg.getMsgType().equals("add")) {//好友请求功能
            Toast.makeText(getApplicationContext(), "有好友申请消息", Toast.LENGTH_SHORT).show();

        }
        if (msg.getMsgType().equals("agree")) {
            BmobRelation relation = new BmobRelation();
            relation.add(msg.getBmobIMUserInfo().getUserId());
            Log.d("agree", "UserInfoID:" + msg.getBmobIMUserInfo().getUserId());

            User myuser = BmobUser.getCurrentUser(User.class);
            myuser.setFriends(relation);
            myuser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {//将申请用户添加至本地好友列表
                    if (e == null) {
                        Log.d("agree", "收到同意添加好友");
                        Toast.makeText(getApplicationContext(), "有新的好友，请刷新通讯录", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("agree", "收到同意添加好友，但添加好友失败");

                    }
                }
            });


        }
        Log.d("IMtest","有在线消息"+msg.getContent());
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
                if (message.getMsgType().equals("add")) {
                    Toast.makeText(getApplicationContext(), "有好友申请消息", Toast.LENGTH_SHORT).show();
                }
                if (message.getMsgType().equals("agree")) {
                    BmobRelation relation = new BmobRelation();
                    User u = new User();
                    u.setObjectId(message.getBmobIMUserInfo().getUserId());
                    relation.add(u);
                    Log.d("agree", "UserInfoID:" + message.getBmobIMUserInfo().getUserId());

                    User myuser = BmobUser.getCurrentUser(User.class);
                    myuser.setFriends(relation);
                    myuser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {//将申请用户添加至本地好友列表
                            if (e == null) {
                                Log.d("agree", "收到同意添加好友");
                                Toast.makeText(getApplicationContext(), "有新的好友，请刷新通讯录", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("agree", "收到同意添加好友，但添加好友失败"+e.getMessage());

                            }
                        }
                    });
                }
                Log.d("IMtest","有离线消息"+message.getContent());
            }
        }
    }
}