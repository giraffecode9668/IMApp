package com.giraffe.imapp.fragment;

import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.User;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class ChatsFragment extends Fragment implements View.OnClickListener{

    boolean isConnect = false;
    boolean isOpenConversation = false;

    EditText et_connect_id,et_receiver_id,et_message;
    Button btn_send,btn_connect;
    TextView tv_message;

    BmobIMConversation mBmobIMConversation;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        et_connect_id = view.findViewById(R.id.FC_ex_local);
        et_receiver_id = view.findViewById(R.id.FC_ex_connect);
        btn_send = view.findViewById(R.id.FC_btn_send);
        btn_connect = view.findViewById(R.id.FC_btn_conncet);
        et_message = view.findViewById(R.id.FC_et_message);
        tv_message = view.findViewById(R.id.FC_tv_message);

        btn_connect.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        user = BmobUser.getCurrentUser(User.class);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.FC_btn_conncet){
            if (TextUtils.isEmpty(et_connect_id.getText().toString())){
                Toast.makeText(getContext(), "请填写连接id", Toast.LENGTH_SHORT).show();
                return;
            }
            btn_connect.setClickable(false);

            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null){
                        isConnect = true;
                        Log.i("TAG","服务器连接成功");
                    }else {
                        Log.i("TAG",e.getMessage()+"  "+e.getErrorCode());
                    }
                }
            });
        }else if (v.getId() == R.id.FC_btn_send){
            if (!isConnect){
                Toast.makeText(getContext(), "未连接状态不能打开会话", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isOpenConversation){
                BmobIMUserInfo info =new BmobIMUserInfo();

                info.setAvatar(user.getAvatar());
                info.setUserId(user.getObjectId());
                info.setName(user.getUsername());

                Log.i("TAG",user.getAvatar()+" "+user.getUsername() + " " + user.getObjectId());


                BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                    @Override
                    public void done(BmobIMConversation c, BmobException e) {
                        if(e==null){
                            isOpenConversation = true;
                            //在此跳转到聊天页面或者直接转化
                            mBmobIMConversation=BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
                            tv_message.append("发送者："+et_message.getText().toString()+"\n");
                            Log.i("TAG","tv_message:"+tv_message.getText().toString());
                            BmobIMTextMessage msg =new BmobIMTextMessage();
                            msg.setContent(et_message.getText().toString());
                            mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                                @Override
                                public void done(BmobIMMessage msg, BmobException e) {
                                    if (e != null) {
                                        et_message.setText("");
                                    }else{
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getContext(), "开启会话出错:"+e.getErrorCode(), Toast.LENGTH_SHORT).show();
                            Log.i("TAG","开启会话错误："+e.getErrorCode()+" : "+e.getStackTrace());
                        }
                    }
                });
            }
            else {
                BmobIMTextMessage msg =new BmobIMTextMessage();
                msg.setContent(et_message.getText().toString());
                tv_message.append("发送者："+et_message.getText().toString()+"\n");
                mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e != null) {
                            et_message.setText("");
                        }else{
                        }
                    }
                });
            }


        }
    }
}
