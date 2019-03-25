package com.giraffe.imapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.activity.LoginActivity;
import com.giraffe.imapp.pojo.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 添加好友适配器
 */
public class AddFriendsAdapter extends ArrayAdapter<User> {

    private int resourceid;

    public AddFriendsAdapter(Context context, int resource,List<User> objects) {//构造函数
        super(context, resource, objects);
        this.resourceid = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final User user = getItem(position);
        ViewHolder viewHolder;

        if (convertView==null){//如果缓存不存在，创建视图
            view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = view.findViewById(R.id.add_friend_avatar);
            viewHolder.nickname = view.findViewById(R.id.add_friend_nickname);
            viewHolder.button = view.findViewById(R.id.add_friend_button);
            view.setTag(viewHolder);
        }else {//如果缓存存在，使用缓存视图
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }


        if (user.getAvatar() != null){//显示头像
            Glide.with(getContext()).load(user.getAvatar().getUrl()).thumbnail(0.1f).
                    placeholder(R.mipmap.ic_launcher).dontAnimate().into(viewHolder.avatar);
        }

        if (user.getNickname()==null){//显示昵称
            viewHolder.nickname.setText("");
        }else {
            viewHolder.nickname.setText(user.getNickname());
        }

        viewHolder.button.setOnClickListener(new View.OnClickListener() {//按钮点击事件
            @Override
            public void onClick(View v) {
                Log.d("addfriend"," " + user.getNickname());

                //发送好友申请消息
                BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(),user.getUsername(),
                        user.getAvatar().getUrl());

                BmobIMConversation conversationEntrance =
                        BmobIM.getInstance().startPrivateConversation(info, true,
                                null);
                BmobIMConversation messageManager =
                        BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

                BmobIMTextMessage msg = new BmobIMTextMessage();
                msg.setContent("好友验证");

                //可随意设置额外信息
                Map<String, Object> map = new HashMap<>();
                map.put("level", "1");
                msg.setExtraMap(map);
                messageManager.sendMessage(msg, new MessageSendListener() {
                    @Override
                    public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                        if (e==null){
                            Log.d("Add","发送成功");
                        }else {
                            Log.d("Add","发送失败");
                        }
                    }
                });
            }
        });
        return view;
    }


    class ViewHolder{//使用ViewHolder和view.Tag的缓存减少加载时间
        CircleImageView avatar;
        TextView nickname;
        Button button;
    }


}
