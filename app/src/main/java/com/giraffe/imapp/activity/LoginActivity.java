package com.giraffe.imapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.User;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //页面组件初始化
    EditText account,password;
    TextView register;
    Intent intent;
    MorphingButton login;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogined();
        initView();
        initListener();

    }

    private void isLogined() {
        if (BmobUser.getCurrentUser(User.class)!=null){
            intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            connectBmobServ();
            finish();
        }
    }

    private void connectBmobServ() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user!=null) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //连接成功
                        Log.d("LoginActivity","connect成功");
                    } else {
                        //连接失败
                        Toast.makeText(LoginActivity.this,
                                "连接IM服务器失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void initView() {
        setContentView(R.layout.activity_login);

        //定位组件
        toolbar = findViewById(R.id.AL_toolbar);
        account = findViewById(R.id.LG_et_account);
        password = findViewById(R.id.LG_pw_password);
        register = findViewById(R.id.LG_tv_register);
        login = findViewById(R.id.LG_mb_login);

        //展示toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }



    private void initListener() {
        //注册事件
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }



    /* ******** */
    /* 点击事件 */
    /* ******** */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LG_tv_register:
                intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                Toast.makeText(this,"注册账号",Toast.LENGTH_LONG).show();
                break;
            case R.id.LG_mb_login:
                if (account.getText().toString().equals("")||password.getText().toString().equals("")){
                    Toast.makeText(this,"请补全账号和密码",Toast.LENGTH_SHORT).show();
                }else {
                    loginByAccount(v);

                }
                break;
        }
    }



     /* ************ */
     /* 账号密码登录 */
    /* ************ */
    private void loginByAccount(View view) {
        BmobUser.loginByAccount(account.getText()+"",
                password.getText()+"", new LogInListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {

                    //按钮变化
                    login.morph(createParams(R.drawable.ic_sure));

                    // 延迟作用，动画效果
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            connectBmobServ();
                            finish();
                            this.cancel();
                        }
                    }, 900);// 这里百毫秒
                } else {
                    Log.d("false:",e.getErrorCode()+" Message:"+e.getMessage());

                    //按钮变化
                    login.morph(createParams(R.drawable.ic_false));

                    if (e.getErrorCode()==9015){
                        Toast.makeText(LoginActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                    }else if (e.getErrorCode()==101){
                        Toast.makeText(LoginActivity.this,
                                "密码或用户错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }



    /* **************** */
    /* 按钮点击后的图形 */
    /* **************** */
    private MorphingButton.Params createParams(int icon){
        MorphingButton.Params btn_clicked = MorphingButton.Params.create()
                .duration(800)
                .cornerRadius(56)
                .color(R.color.mb_blue_dark)
                .width(200)
                .height(150)
                .colorPressed(R.color.mb_blue_dark);
        btn_clicked.icon(icon);
        return btn_clicked;
    }



    /* ************ */
    /* 刷新当前页面 */
    /* ************ */
    public static void restartActivity(Activity activity){
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
    }


}
