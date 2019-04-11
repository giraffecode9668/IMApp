package com.giraffe.imapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.giraffe.imapp.activity.ShowIfmActivity;
import com.giraffe.imapp.adapter.FriendsAdapter;
import com.giraffe.imapp.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

import static com.giraffe.imapp.url.IsConnected.isNetworkConnected;

public class FriendsFragment extends Fragment  implements AdapterView.OnItemClickListener {

    View view;//Fragment视图
    private List<User> userList = new ArrayList<>();//要显示的好友列表数据
    PtrClassicFrameLayout ptrClassicFrameLayout;//下拉框
    ListView listView;
    FriendsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载页面
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        //初始组件
        ptrClassicFrameLayout = view.findViewById(R.id.FF_pcfl_update);
        listView = view.findViewById(R.id.FF_lv_friends);

        initListener();//行为动作

        return view;
    }



    private void initListener() {
        refreshList();//打开页面将自动刷新一次

        //下拉刷新设置
        ptrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshList();//下拉手动刷新
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrClassicFrameLayout.refreshComplete();
                    }
                }, 1200);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Toast.makeText(getContext(),"点击了："+userList.get(position).getNickname(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(),ShowIfmActivity.class);
        User cuser = userList.get(position);
        Bundle data = new Bundle();
        data.putString("userid",cuser.getObjectId());
        data.putString("username",cuser.getUsername());
        data.putString("nickname",cuser.getNickname());
        data.putString("sign",cuser.getSign());
        data.putString("avatar",cuser.getAvatar().getUrl());
        data.putString("sex",cuser.getSex());
        data.putString("space",cuser.getSpace());

        intent.putExtra("data",data);
        startActivity(intent);
    }



    /* **************** */
    /* 更新ListView数据 */
    /* **************** */
    private void refreshList() {
        initUser();//获得好友数据userList
        ListSize listSize = new ListSize();
        listSize.size = userList.size();

        //如果查询得到的userList中好友数目和之前不一样，执行刷新，避免重复setAdapter
        if (view.getTag()==null){
            //根据userList和子项id创建适配器
            adapter = new FriendsAdapter(getContext(),R.layout.listitem_friends,userList);

            listView.setAdapter(adapter);//显示适配器内容
            Log.d("refresh","执行了apdater1"+listView.getAdapter().toString());
            view.setTag(listSize);

        }else if(((ListSize) view.getTag()).size!=userList.size()){
            adapter = new FriendsAdapter(getContext(),R.layout.listitem_friends,userList);
            listView.setAdapter(adapter);//显示适配器内容
            Log.d("refresh","执行了apdater2"+listView.getAdapter().toString());
            view.setTag(listSize);

        }else {
            Log.d("refresh","执行了apdater3"+listView.getAdapter().toString());
        }

    }



    /* **************** */
    /* 获得userList数据 */
    /* **************** */
    private void initUser() {
        BmobQuery<User> query = new BmobQuery<>();

        final User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("friends",new BmobPointer(user));//查询语句
        boolean isCache = query.hasCachedResult(User.class);//本地是否存在缓存
        if(isNetworkConnected(getContext())){//判断网络情况，有网络下
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//查询先缓存再网络
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));//此表示缓存7天
            Log.d("init","缓存7天");
        }else {//在没有网络下
            if (isCache){//本地存在缓存
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//只查询本地缓存
                Log.d("init","获得缓存查询");
            }else {//本地没有缓存
                Toast.makeText(getContext(),"本地存储过期，请连接网络",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //获取缓存或查询操作中的好友数据
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    Log.d("init","查询个数："+list.size());
                    if (userList.isEmpty()){//userList为空，添加
                        userList.addAll(list);
                    }else if(!userList.equals(list)){//userList不等于存储，刷新
                        userList.clear();
                        userList.addAll(list);
                    }//userList等于存储中的list，不更改
                }else {
                    Log.d("init","失败"+e.getMessage());
                }
            }
        });
    }



    /* ******************************************************* */
    /* 缓存在view中的UserList的长度，用于判断是否再创建Apapter */
    /* ******************************************************* */
    class ListSize{
        int size;
    }



    /* **************** */
    /* 手动添加测试数据 */
    /* **************** */
    private void addFriends(){
        if (BmobUser.getCurrentUser(User.class)!=null){
            User user1 = new User();
            user1.setObjectId("17796ebc63");

            User user2 = new User();
            user2.setObjectId("2aae0d2ad8");

            User user3 = new User();
            user3.setObjectId("17796ebc63");

            User user4 = new User();
            user4.setObjectId("a628d94ee7");

            User user5 = new User();
            user5.setObjectId("60f72be4f3");

            BmobRelation relation = new BmobRelation();
            relation.add(user1);
            relation.add(user2);
            relation.add(user3);
            relation.add(user4);
            relation.add(user5);


            User myuser = BmobUser.getCurrentUser(User.class);
            myuser.setFriends(relation);
            myuser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        Log.d("initUser","添加成功");
                    }else {
                        Log.d("initUser",e.getMessage());
                    }

                }
            });
        }
    }

}
