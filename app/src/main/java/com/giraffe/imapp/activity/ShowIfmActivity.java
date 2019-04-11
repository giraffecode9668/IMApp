package com.giraffe.imapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;


import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowIfmActivity extends AppCompatActivity {

    String nickname,username,avatar,sign,sex,space,uid;//提取通讯传入的数据，以便不用网上提取数据
    Button opens;//打开会话按钮
    Bundle data;//传入的数据data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ifm);

        getContent();//提取传入的数据
        initView();//界面
        initListener();//动作
    }

    private void getContent() {
        data = getIntent().getBundleExtra("data");
        uid = data.getString("userid");
        nickname = data.getString("nickname");
        username = data.getString("username");
        avatar = data.getString("avatar");
        sign = data.getString("sign");
        sex = data.getString("sex");
        space = data.getString("space");
    }

    private void initListener() {
        //打开会话按钮点击事件，创建会话，跳转谈话
        opens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建用户信息
                BmobIMUserInfo info = new BmobIMUserInfo(uid, nickname, avatar);
                //根据当前用户信息创建常态会话接口
                BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);

                //跳转至谈话窗
                Intent intent = new Intent(ShowIfmActivity.this,ChatActivity.class);
                intent.putExtra("data",data);//将data数据传入
                intent.putExtra("c",conversationEntrance);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        CircleImageView circleImageView = findViewById(R.id.AS_civ_avatar);
        TextView title = findViewById(R.id.AS_toolbar_title);
        TextView tv_nickname = findViewById(R.id.AS_tv_nickname);
        TextView tv_sign = findViewById(R.id.AS_tv_sign);
        TextView tv_username = findViewById(R.id.AS_tv_username);
        TextView tv_sex = findViewById(R.id.AS_tv_sex);
        TextView tv_space = findViewById(R.id.AS_tv_space);
        opens = findViewById(R.id.AS_btn_session);
        Glide.with(this).load(avatar).thumbnail(0.1f).dontAnimate().into(circleImageView);
        title.setText(nickname);
        tv_nickname.setText(nickname);
        tv_sign.setText(sign);
        tv_username.setText(username);
        tv_sex.setText(sex);
        tv_space.setText(space);
    }
}
