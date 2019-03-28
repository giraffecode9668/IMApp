package com.giraffe.imapp.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.AgreeAddFriendMessage;
import com.giraffe.imapp.pojo.ConFriend;
import com.giraffe.imapp.pojo.User;

import java.util.List;
import java.util.Queue;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.dao.query.Query;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class ConfirmFriendAdapter extends ArrayAdapter<ConFriend> {

    int resourceid;
    ViewHolder viewHolder;

    public ConfirmFriendAdapter(Context context, int resource,  List<ConFriend> objects) {
        super(context, resource, objects);
        resourceid = resource;
        Log.d("adapter","申请适配器");

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ConFriend conFriend = getItem(position);
        final User sender = conFriend.getSender();

        Log.d("cadapter",""+sender.getNickname());

        View view;
        if (convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = view.findViewById(R.id.confirmfriend_avatar);
            viewHolder.nickname = view.findViewById(R.id.confirmfriend_nickname);
            viewHolder.content = view.findViewById(R.id.confirmfriend_content);
            viewHolder.refuse = view.findViewById(R.id.confirmfriend_refuse);
            viewHolder.pass = view.findViewById(R.id.confirmfriend_pass);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        final View btn_pass = viewHolder.pass;
        final View btn_refuse = viewHolder.refuse;

        if (sender.getAvatar()!=null){
            Glide.with(getContext()).load(sender.getAvatar().getUrl()).thumbnail(0.1f).dontAnimate()
                    .into(viewHolder.avatar);//显示头像
        }
        if (sender.getNickname()!=null){
            viewHolder.nickname.setText(sender.getNickname());//显示昵称
        }
        if (conFriend.getContent()!=null){
            viewHolder.content.setText(conFriend.getContent());//显示内容
        }




        viewHolder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击拒绝按钮操作
                btn_refuse.setClickable(false);
                ((Button) btn_refuse).setText("拒绝");
                ((Button) btn_refuse).setTextColor(
                        viewHolder.pass.getResources().getColor(R.color.colorPrimary));
                btn_pass.setVisibility(View.GONE);//设置按钮状态


                //同步表格ConFriend拒绝状态码 1
                conFriend.setStatus(1);
                conFriend.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){

                        }
                    }
                });
            }
        });



        viewHolder.pass.setOnClickListener(new View.OnClickListener() {//同意操作
            @Override
            public void onClick(View v) {
                BmobRelation relation = new BmobRelation();
                relation.add(sender);
                User myuser = BmobUser.getCurrentUser(User.class);
                myuser.setFriends(relation);
                myuser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {//将申请用户添加至本地好友列表
                        if (e==null){
                            btn_pass.setClickable(false);
                            ((Button) btn_pass).setText("同意");
                            ((Button) btn_pass).setTextColor(
                                    viewHolder.pass.getResources().getColor(R.color.colorPrimary));
                            btn_refuse.setVisibility(View.GONE);
                            Log.d("initUser","添加成功");
                            Log.d("cadapter","添加成功："+sender.getNickname());

                            sendAgressMessage(sender);//发送同意添加好友

                        }else {
                            Log.d("initUser",e.getMessage());
                        }
                    }
                });

                //同步表格ConFriend拒绝状态码 2
                conFriend.setStatus(2);
                conFriend.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){

                        }
                    }
                });
            }
        });


        //获得网络上的历史记录并按状态码显示‘同意’‘拒绝’按钮
        if (conFriend.getStatus()==1){//状态码，1为拒绝，2为接受;  拒绝只显示拒绝按钮
            btn_refuse.setClickable(false);
            ((Button) btn_refuse).setText("拒绝");
            ((Button) btn_refuse).setTextColor(
                    viewHolder.pass.getResources().getColor(R.color.colorPrimary));
            btn_pass.setVisibility(View.GONE);
        }else if(conFriend.getStatus()==2){//同意只显示同意按钮
            btn_pass.setClickable(false);
            ((Button) btn_pass).setText("同意");
            ((Button) btn_pass).setTextColor(
                    viewHolder.pass.getResources().getColor(R.color.colorPrimary));
            btn_refuse.setVisibility(View.GONE);
        }

        return view;
    }


    private void sendAgressMessage(User sender){
        BmobIMUserInfo senderinfo = new BmobIMUserInfo(sender.getObjectId(),sender.getUsername(),
                sender.getAvatar().getUrl());
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(senderinfo, true, null);
        BmobIMConversation messageManager =
                BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        msg.setContent("同意你的好友请求");
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e==null){
                    Log.d("agree","发送成功");
                    Toast.makeText(getApplicationContext(),"发送同意好友请求成功",Toast.LENGTH_SHORT).show();

                }else {
                    Log.d("agree","发送失败");
                    Toast.makeText(getApplicationContext(),"发送同意好友请求失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class ViewHolder{
        CircleImageView avatar;
        TextView nickname,content;
        Button refuse,pass;
    }

}

