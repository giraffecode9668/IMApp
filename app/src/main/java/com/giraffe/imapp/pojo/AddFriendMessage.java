package com.giraffe.imapp.pojo;

import android.text.TextUtils;

import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * 自定义消息——发送好友验证，message类型为“add”
 */
public class AddFriendMessage  extends BmobIMExtraMessage {

    public static final String ADD = "add";

    public AddFriendMessage() {
    }


    @Override
    public String getMsgType() {
        return ADD;
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

}
