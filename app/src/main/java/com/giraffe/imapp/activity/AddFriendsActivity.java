package com.giraffe.imapp.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.giraffe.imapp.adapter.AddFriendsAdapter;
import com.giraffe.imapp.pojo.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendsActivity extends AppCompatActivity {

    //查询用户得出的结果集，因为模糊查询收费，所以一般为一个元素
    private List<User> userList = new ArrayList<>();
    private EditText editText;//输入框
    private Button button;//搜索按钮
    private ListView listView;//ListView
    private String username;//输入框的内容


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();//加载视图
        initListener();//监听事件

    }



    /* ********** */
    /* 初始化界面 */
    /* ********** */
    private void initView() {
        setContentView(R.layout.activity_add_friends);

        listView =  findViewById(R.id.lv_add_friends);
        editText = findViewById(R.id.et_add_friends);
        button = findViewById(R.id.btn_add_friends);
    }



    /* ********* */
    /* 监听事件 */
    /* ********* */
    private void initListener() {
            button.setOnClickListener(new View.OnClickListener() {//搜索按钮点击执行查询操作
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")){
                    username = editText.getText().toString();
                    initUser();//获得userList数据并将结果显示
                }else {//如果没有输入将提示输入账号
                    Toast.makeText(AddFriendsActivity.this,"请输入账号",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    /* **************** */
    /* 获得userList数据 */
    /* **************** */
    private void initUser() {
        BmobQuery<User> query = new BmobQuery<>();
        userList.clear();//清除数据保证每次都只显示当前结果

        query.addWhereEqualTo("username",username);//查询语句
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    Log.d("init","个数："+list.size());
                    userList.addAll(list);

                    //即时添加适配器
                    AddFriendsAdapter addFriendsAdapter = new AddFriendsAdapter(
                            AddFriendsActivity.this,R.layout.listitem_addfriends,userList);
                    listView.setAdapter(addFriendsAdapter);

                    if (list.size()==0){//如果查询结果没有数据，提示不存在用户
                        Toast.makeText(AddFriendsActivity.this,"不存在用户",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {//查询失败，一般是无网络
                    Log.d("init","失败"+e.getMessage());
                    Toast.makeText(AddFriendsActivity.this,"查询失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
