package com.giraffe.imapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.giraffe.imapp.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText account,password;
    Button register,login;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bmob.initialize(this, "38950169a9ad0b92f51e7e2537eeadb3");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        account = findViewById(R.id.LG_et_account);
        password = findViewById(R.id.LG_pw_password);
        register = findViewById(R.id.LG_btn_register);
        login = findViewById(R.id.LG_btn_login);

        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LG_btn_register:
                intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                Toast.makeText(this,"注册账号",Toast.LENGTH_LONG).show();
                break;
            case R.id.LG_btn_login:
                loginByAccount(v);
                break;
        }
    }

    /**
     * 账号密码登录
     */
    private void loginByAccount(final View view) {
        //此处替换为你的用户名密码

        BmobUser.loginByAccount(account.getText()+"", password.getText()+"", new LogInListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {
                    intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "密码或用户错误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
