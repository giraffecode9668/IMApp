package com.giraffe.imapp.pojo;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private String content,ravatar,savatar;
    private  int type;


    public Msg(String content,int type,String ravatar,String savatar){
        this.content = content;
        this.type = type;
        this.ravatar = ravatar;
        this.savatar = savatar;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getReceiver() {
        return ravatar;
    }

    public String getSender() {
        return savatar;
    }

}
