package com.giraffe.imapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.giraffe.imapp.R;
import com.giraffe.imapp.adapter.ConfirmFriendAdapter;
import com.giraffe.imapp.pojo.ConFriend;
import com.giraffe.imapp.pojo.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ConfirmFriendActivity extends AppCompatActivity {

    ListView listView;
    List<ConFriend> conFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }



    private void initView() {
        setContentView(R.layout.activity_confirm_friend);
        listView = findViewById(R.id.lv_confirmfriend);

        initData();//获取数据并为listView添加适配器
    }



    /* ****************************** */
    /* 获取数据并为listView添加适配器 */
    /* ****************************** */
    private void initData() {
        BmobQuery<ConFriend> query = new BmobQuery<>();
        query.addWhereEqualTo("receiver", BmobUser.getCurrentUser(User.class));
        query.order("-createdAt");
        query.include("sender");

        //Todo 可以设置本地缓存，当没有网的时候可以查到历史记录

        query.findObjects(new FindListener<ConFriend>() {
            @Override
            public void done(List<ConFriend> list, BmobException e) {//查询ConFriend表获得好友申请列表，包括历史好友申请
                if (e == null) {
                    if (list!=null){
                        conFriends.addAll(list);
                        Log.d("confirm","申请列表："+list.size());
                        Log.d("confirm","申请列表："+conFriends.toString());
                        ConfirmFriendAdapter adapter = new ConfirmFriendAdapter(
                                ConfirmFriendActivity.this,R.layout.listitem_confriend,conFriends);
                        listView.setAdapter(adapter);
                    }else {
                        Log.d("confirm","申请列表"+list.toString());
                    }
                } else {
                    Log.d("confirm","申请列表失败");
                }
            }
        });
    }
}
