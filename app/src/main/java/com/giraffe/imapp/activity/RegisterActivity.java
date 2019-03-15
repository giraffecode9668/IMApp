package com.giraffe.imapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.giraffe.imapp.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //页面组件初始化
    EditText account,password;
    Button register;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //定位组件
        account = findViewById(R.id.RG_et_account);
        password = findViewById(R.id.RG_pw_password);
        register = findViewById(R.id.RG_btn_register);

        //监听注册事件
        register.setOnClickListener(this);
    }

    /**
     * 点击事件
     **/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.RG_btn_register:
                intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                signUp(v);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 账号密码注册方法
     */
    private void signUp(final View view) {
        final BmobUser user = new BmobUser();
        user.setUsername(account.getText().toString());
        user.setPassword(password.getText().toString());

        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();

                    System.out.print(e.getStackTrace()+"e.getErrorCode()："+e.getErrorCode());
                }
            }
        });
    }
}
