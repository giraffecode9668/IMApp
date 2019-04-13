package com.giraffe.imapp.url;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.giraffe.imapp.activity.ChatActivity;
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
    int number = 0;
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
        NotificationManager manager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Context context = getApplicationContext();
        Notification notification = null;
        String id = "my_channel_01";
        String name="我是渠道名字";
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        BmobIMConversation bmobIMConversation = msg.getBmobIMConversation();
        Log.d("IMMessage",bmobIMConversation.toString());
        Log.d("IMMessage",bmobIMConversation.getConversationTitle());
        Log.d("IMMessage",bmobIMConversation.getConversationIcon());

        intent.putExtra("c",bmobIMConversation);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(bmobIMConversation.getConversationTitle())
                    .setContentText(msg.getContent())
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(bmobIMConversation.getConversationTitle())
                    .setContentText(msg.getContent())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }


        manager.notify(1,notification);

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