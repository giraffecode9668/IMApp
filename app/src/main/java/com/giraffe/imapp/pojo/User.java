package com.giraffe.imapp.pojo;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser {
    private BmobFile avatar;
    private String nickname;
    private int sex;
    private String space;
    private String sign;


    private String mood;

    public User(){}

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        if (sex==0){
            return "未知";
        }else if (sex==1){
            return "男";
        }else {
            return "女";
        }
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

//    public User(NewFriend friend){
//        setObjectId(friend.getUid());
//        setUsername(friend.getName());
//        setAvatar(friend.getAvatar());
//    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}
