package com.giraffe.imapp.base;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.giraffe.imapp.pojo.User;
import com.giraffe.imapp.url.IsConnected;

import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        if (IsConnected.isNetworkConnected(getApplicationContext())){
            if (BmobIMClient.getInstance().getCurrentConnectStatus().getCode()!=1){
                User user = BmobUser.getCurrentUser(User.class);
                BmobIMClient.connect(user.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        Log.d("IMConnect","connected success");
                    }
                });
            }
        }else{
            Toast.makeText(getApplicationContext(),"未连接网络",Toast.LENGTH_SHORT).show();

        }

    }
}
