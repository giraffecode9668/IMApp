package com.giraffe.imapp.pojo;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private String content;
    private  int type;
    private User receiver,sender;

    public Msg(String content,int type,User ruser,User suser){
        this.content = content;
        this.type = type;
        this.receiver = ruser;
        this.sender = suser;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public User getReceiver() {
        return receiver;
    }

    public User getSender() {
        return sender;
    }

}
