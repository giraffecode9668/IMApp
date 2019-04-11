package com.giraffe.imapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.Msg;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> messageList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout leftLayout;
        RelativeLayout rightLayout;
        CircleImageView leftAvatar;
        CircleImageView rightAvatar;
        TextView leftText;
        TextView rightText;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.chat_at_left);
            rightLayout = view.findViewById(R.id.chat_at_right);
            leftText = view.findViewById(R.id.chat_at_left_text);
            rightText = view.findViewById(R.id.chat_at_right_text);
            leftAvatar = view.findViewById(R.id.chat_at_left_avatar);
            rightAvatar = view.findViewById(R.id.chat_at_right_avatar);
        }
    }

    public MsgAdapter(List<Msg> messageList){
        this.messageList = messageList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_msg,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = messageList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED){
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftText.setText(msg.getContent());
            Glide.with(context).load(msg.getReceiver()).thumbnail(0.1f).dontAnimate().into(holder.leftAvatar);
        }else if(msg.getType() == Msg.TYPE_SEND){
            //如果是发送的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightText.setText(msg.getContent());
            Glide.with(context).load(msg.getSender()).thumbnail(0.1f).dontAnimate().into(holder.rightAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
