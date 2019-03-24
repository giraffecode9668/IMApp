package com.giraffe.imapp.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.giraffe.imapp.R;
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

public class FriendsFragment extends Fragment {

    private List<User> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        initUser();
        ListView listView = view.findViewById(R.id.FF_lv_friends);
        FriendsAdapter adapter = new FriendsAdapter(getContext(),R.layout.listitem_friends,userList);
        listView.setAdapter(adapter);

        return view;
    }



    private void initUser() {
        BmobQuery<User> query = new BmobQuery<>();

        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("friends",new BmobPointer(user));
        boolean isCache = query.hasCachedResult(User.class);
        if(isNetworkConnected(getContext())){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));//此表示缓存7天
            Log.d("init","缓存7天");
        }else {
            if (isCache){
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
                Log.d("init","获得缓存查询");
            }else {
                Toast.makeText(getContext(),"本地存储过期，请连接网络",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    Log.d("init","查询个数："+list.size());
                    for (User u:list){
                        userList.add(u);
                    }
                }else {
                    Log.d("init","失败"+e.getMessage());
                }
            }
        });

    }

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

    /* **************** */
    /* 判断网络是否连接 */
    /* **************** */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
