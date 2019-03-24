package com.giraffe.imapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<User> {
    private int resourceId;

    public FriendsAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);//获取当前项的User实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null){//如果缓存存在，使用缓存视图
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.civ_avatar = view.findViewById(R.id.friend_avatar);
            viewHolder.tv_nickname = view.findViewById(R.id.friend_nickname);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (user.getAvatar() != null){
            Glide.with(getContext()).load(user.getAvatar().getUrl()).thumbnail(0.1f).
                    placeholder(R.mipmap.ic_launcher).dontAnimate().into(viewHolder.civ_avatar);
        }
        if (user.getNickname()==null){
            viewHolder.tv_nickname.setText("");
        }else {
            viewHolder.tv_nickname.setText(user.getNickname());
        }

        return view;
    }

    class ViewHolder{
        CircleImageView civ_avatar;
        TextView tv_nickname;
    }
}
