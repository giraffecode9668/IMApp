package com.giraffe.imapp.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.giraffe.imapp.activity.AddFriendsActivity;
import com.giraffe.imapp.activity.LoginActivity;
import com.giraffe.imapp.pojo.AddFriendMessage;
import com.giraffe.imapp.pojo.ConFriend;
import com.giraffe.imapp.pojo.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.giraffe.imapp.url.IsConnected.isNetworkConnected;

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

        final  Button button = viewHolder.button;


        //查询是否该用户已经是好友
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("friends",new BmobPointer(BmobUser.getCurrentUser(User.class)));//查询本地用户的好友
        boolean isCache = query.hasCachedResult(User.class);//本地是否存在缓存
        if(isNetworkConnected(getContext())){//判断网络情况，有网络下
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//查询先缓存再网络
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));//此表示缓存7天
            Log.d("init","缓存7天");
        }else {//在没有网络下
            if (isCache){//本地存在缓存
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//只查询本地缓存
                Log.d("init","获得缓存查询");
            }else {//本地没有缓存
                Toast.makeText(getContext(),"本地存储过期，请连接网络",Toast.LENGTH_SHORT).show();
            }
        }

        //获取缓存或查询操作中的好友数据
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    for (User u:list){
                        if (u.getUsername().equals(user.getUsername())){
                            button.setVisibility(View.GONE);
                            Log.d("重复","好友已经存在");
                        }
                    }
                }else {
                    Log.d("init","失败"+e.getMessage());
                }
            }
        });

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

                final AddFriendMessage msg = new AddFriendMessage();//使用自定义类型message:add
                msg.setContent("你好，很高兴认识你！");


                messageManager.sendMessage(msg, new MessageSendListener() {//发送消息
                    @Override
                    public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                        if (e==null){
                            Log.d("Add","发送成功");
                            ConFriend conFriend = new ConFriend();
                            conFriend.setSender(BmobUser.getCurrentUser(User.class));
                            conFriend.setReceiver(user);
                            conFriend.setContent(msg.getContent());
                            conFriend.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e==null){
                                        Log.d("Add","插入表数据成功");
                                        Toast.makeText(getApplicationContext(),"成功发送好友请求",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Log.d("Add","插入表数据失败");
                                        Toast.makeText(getApplicationContext(),"发送好友请求失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Log.d("Add","发送失败");
                            Toast.makeText(getApplicationContext(),"发送好友请求失败",Toast.LENGTH_SHORT).show();
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
