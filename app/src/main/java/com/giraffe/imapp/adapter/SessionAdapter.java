package com.giraffe.imapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.Conversation;
import com.giraffe.imapp.pojo.User;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import de.hdodenhof.circleimageview.CircleImageView;

public class SessionAdapter extends ArrayAdapter<BmobIMConversation> {

    private int resourceid;

    public SessionAdapter(Context context, int resource, List<BmobIMConversation> objects) {
        super(context, resource, objects);
        this.resourceid = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobIMConversation conversation = getItem(position);//获取当前项的Conversation实例
        View view;//子项视图
        ViewHolder viewHolder;
        if (convertView == null){//如果缓存不存在，创建视图
            view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.civ_avatar = view.findViewById(R.id.friend_avatar);
            viewHolder.tv_nickname = view.findViewById(R.id.friend_nickname);
            view.setTag(viewHolder);

        }else {//如果缓存存在，使用缓存视图
            view = convertView;
            viewHolder =  (ViewHolder) view.getTag();

        }

        if (conversation.getConversationIcon() != null){//显示头像
            Glide.with(getContext()).load(conversation.getConversationIcon()).thumbnail(0.1f).
                    placeholder(R.mipmap.ic_launcher).dontAnimate().into(viewHolder.civ_avatar);
        }

        if (conversation.getConversationTitle()==null){//显示用户昵称
            viewHolder.tv_nickname.setText("");
        }else {
            viewHolder.tv_nickname.setText(conversation.getConversationTitle());
        }

        return view;
    }

    class ViewHolder{
        CircleImageView civ_avatar;
        TextView tv_nickname;
    }
}
