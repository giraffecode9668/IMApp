package com.giraffe.imapp.pojo;

import cn.bmob.newim.bean.BmobIMConversation;

public class Conversation {

    public Conversation(BmobIMConversation IMconversation){
        this.cId = IMconversation.getId().toString();
        this.cName = IMconversation.getConversationTitle();
        this.cType = IMconversation.getConversationType();
        this.Avatar = IMconversation.getConversationIcon();
    }

    /**
     * 会话id
     */
    protected String cId;
    /**
     * 会话类型
     */
    protected int cType;
    /**
     * 会话名称
     */
    protected String cName;

    /**
     * 获取头像-用于会话界面显示
     */
    protected String Avatar;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }
}
