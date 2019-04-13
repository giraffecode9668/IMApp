package com.giraffe.imapp.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.giraffe.imapp.adapter.MsgAdapter;
import com.giraffe.imapp.base.BaseActivity;
import com.giraffe.imapp.pojo.Msg;
import com.giraffe.imapp.pojo.User;
import com.giraffe.imapp.url.IsConnected;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class ChatActivity extends BaseActivity implements MessageListHandler {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private TextView title;
    private Button send;
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private String avatar,nickname;//对方头像、昵称
    User suser;//本地用户
    String savater;//本地用户头像
    Handler handler;//线程管理、用于监控获取信息并显示

    //消息管理器
    BmobIMConversation mConversationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        //获得当前会话
        BmobIMConversation bmobIMConversation = (BmobIMConversation) getIntent().getSerializableExtra("c");
        avatar = bmobIMConversation.getConversationIcon();
        nickname = bmobIMConversation.getConversationTitle();

        //当前会话管理器
        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), bmobIMConversation);
        initView();
        initListener();
    }



    /**
     * 注册BmobIM消息监听器
     */
    @Override
    protected void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
    }



    /**
     * 解除BmobIM消息监听器
     */
    @Override
    protected void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }


    /**
     * 初始化页面
     */
    private void initView() {
        inputText = findViewById(R.id.ASN_et_input);
        send = findViewById(R.id.ASN_btn_send);
        recyclerView = findViewById(R.id.ASN_rv_recycleView);
        title = findViewById(R.id.AS_toolbar_title);
        suser = BmobUser.getCurrentUser(User.class);
        savater = suser.getAvatar().getFileUrl();

        title.setText(nickname);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);

        initMsgs();//打开界面获取聊天记录
    }


    /**
     * 监听
     */
    private void initListener() {

        //发送按钮事件
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_SEND,avatar,savater);
                    msgList.add(msg);
                    msgAdapter.notifyItemChanged(msgList.size()-1);
                    recyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");

                    BmobIMTextMessage bmobIMTextMessage = new BmobIMTextMessage();
                    bmobIMTextMessage.setContent(content);

                    Log.d("SessionActivity","要发送消息："+bmobIMTextMessage.getContent());

                    mConversationManager.sendMessage(bmobIMTextMessage, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                            if (e == null){
                                Toast.makeText(getApplicationContext(),"发送消息成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(),"发送消息失败",Toast.LENGTH_SHORT).show();
                                Log.d("SessionActivity","e:"+e.getMessage());
                            }

                        }
                    });
                }
            }
        });
    }



    private void initMsgs() {
        mConversationManager.queryMessages(null, 25, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        msgList.clear();
                        for (BmobIMMessage m : list){
                            if (m.getFromId().equals(suser.getObjectId())){
                                Msg msg = new Msg(m.getContent(),Msg.TYPE_SEND,avatar,savater);
                                msgList.add(msg);
                            }else {
                                Msg msg = new Msg(m.getContent(),Msg.TYPE_RECEIVED,avatar,savater);
                                msgList.add(msg);
                            }
                        }
                        msgAdapter = new MsgAdapter(msgList);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setAdapter(msgAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.scrollToPosition(msgList.size()-1);

                    }else {
                        Toast.makeText(getApplicationContext(),"无数据",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"打开错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //获得在线消息添加到msgList并同时更新Adapter和recyclerView
         handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123){
                    msgList.add((Msg) msg.obj);
                    msgAdapter.notifyItemChanged(msgList.size()-1);
                    recyclerView.scrollToPosition(msgList.size()-1);
                }
            }
        };

        msgAdapter = new MsgAdapter(msgList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setAdapter(msgAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(msgList.size()-1);
    }



    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            Log.d("SessionActivity","发送开始");

        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            Log.d("SessionActivity","发送over1");
            if(e!=null){
                Log.d("SessionActivity",e.getMessage()+"");
            }
            if (e==null){
                Log.d("SessionActivity","发送over2");
            }

        }
    };



    //处理在线消息、离线消息
    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        //接收处理在线、离线消息
        for (MessageEvent m : list){

            if (m.getFromUserInfo().getName().equals(mConversationManager.getConversationTitle())){
                final Msg msg = new Msg(m.getMessage().getContent(),Msg.TYPE_RECEIVED,avatar,savater);
                Message message = new Message();
                message.what = 0x123;
                message.obj = msg;
                handler.sendMessage(message);
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(1);
            }else {

            }
        }
    }
}
