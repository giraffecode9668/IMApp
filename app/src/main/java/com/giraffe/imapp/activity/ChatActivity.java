package com.giraffe.imapp.activity;

import android.os.Bundle;
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
import com.giraffe.imapp.pojo.Msg;
import com.giraffe.imapp.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ChatActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private TextView title;
    private Button send;
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private String avatar,nickname;
    User suser;
    String savater;

    BmobIMConversation mConversationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        BmobIMConversation bmobIMConversation = (BmobIMConversation) getIntent().getSerializableExtra("c");//可能是没有info
        avatar = bmobIMConversation.getConversationIcon();
        nickname = bmobIMConversation.getConversationTitle();

        Log.d("SessionActivity",bmobIMConversation.getConversationType()+" ");

        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), bmobIMConversation);
        initView();
        initListener();
    }

    private void initListener() {
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

    private void initView() {
        inputText = findViewById(R.id.ASN_et_input);
        send = findViewById(R.id.ASN_btn_send);
        recyclerView = findViewById(R.id.ASN_rv_recycleView);
        title = findViewById(R.id.AS_toolbar_title);
        suser = BmobUser.getCurrentUser(User.class);
        savater = suser.getAvatar().getFileUrl();

        initMsgs();


        title.setText(nickname);

        mConversationManager.queryMessages(null, 50, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
                       for (BmobIMMessage m : list){
                           Log.d("SessionActivity","querry:"+m.getContent());
                       }
                    }
                } else {
                    Log.d("SessionActivity","querry:"+e.getMessage());
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

}
