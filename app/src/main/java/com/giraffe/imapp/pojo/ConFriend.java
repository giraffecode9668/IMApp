package com.giraffe.imapp.pojo;

import cn.bmob.v3.BmobObject;

public class ConFriend extends BmobObject {
    private User receiver;
    private User sender;
    private String content;
    private int status;


    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



}
