package com.giraffe.imapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowIfmActivity extends AppCompatActivity {

    String nickname,username,avatar,sign,sex,space;
    Button opens;
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ifm);
        data = getIntent().getBundleExtra("data");
        nickname = data.getString("nickname");
        username = data.getString("username");
        avatar = data.getString("avatar");
        sign = data.getString("sign");
        sex = data.getString("sex");
        space = data.getString("space");
        initView();
        initListener();
    }

    private void initListener() {
        opens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowIfmActivity.this,SessionActivity.class);
                intent.putExtra("data",data);
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
