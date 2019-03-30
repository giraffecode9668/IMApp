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

import com.giraffe.imapp.R;
import com.giraffe.imapp.adapter.MsgAdapter;
import com.giraffe.imapp.pojo.Msg;
import com.giraffe.imapp.pojo.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SessionActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private TextView title;
    private Button send;
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    Bundle data;
    User ruser,suser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        data = getIntent().getBundleExtra("data");

        initView();

        initListener();
    }

    private void initListener() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_SEND,ruser,suser);
                    msgList.add(msg);
                    msgAdapter.notifyItemChanged(msgList.size()-1);
                    recyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
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
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username",data.getString("username"));
        Log.d("session",data.getString("username"));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    Log.d("session","chenggon:"+list.get(0).getNickname());
                    ruser = list.get(0);
                    initMsgs();
                }
            }
        });

        title.setText(data.getString("nickname"));





    }

    private void initMsgs() {
        Msg msg1 = new Msg("你好，世界！",Msg.TYPE_RECEIVED,ruser,suser);
        msgList.add(msg1);
        Msg msg2 = new Msg("你好，世界！",Msg.TYPE_SEND,ruser,suser);
        msgList.add(msg2);
        Msg msg3 = new Msg("你好，世界！",Msg.TYPE_RECEIVED,ruser,suser);
        msgList.add(msg3);
        Msg msg4 = new Msg("你好，世界！",Msg.TYPE_SEND,ruser,suser);
        msgList.add(msg4);
        msgAdapter = new MsgAdapter(msgList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(msgAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
