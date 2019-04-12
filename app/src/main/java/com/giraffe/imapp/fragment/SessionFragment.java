package com.giraffe.imapp.fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.giraffe.imapp.R;
import com.giraffe.imapp.activity.ChatActivity;
import com.giraffe.imapp.adapter.SessionAdapter;
import com.giraffe.imapp.base.BaseFragment;
import com.giraffe.imapp.pojo.Conversation;
import com.giraffe.imapp.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class SessionFragment extends BaseFragment {

    View view;
    PtrClassicFrameLayout ptrClassicFrameLayout;
    ListView listView;
    List<BmobIMConversation> conversationList;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        initView();
        initListener();

        return view;
    }

    private void initView() {
        //初始组件
        ptrClassicFrameLayout = view.findViewById(R.id.FC_pcfl_update);
        listView = view.findViewById(R.id.FC_lv_friends);

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

    }


    private void initListener() {
        //进入页面，自动刷新
        refreshList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BmobIMConversation conversation = conversationList.get(position);

                Intent intent = new Intent(getContext(),ChatActivity.class);
                intent.putExtra("c",conversation);
                startActivity(intent);
            }
        });
    }

    private void refreshList(){
        conversationList = new ArrayList<>();
        conversationList.clear();

        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if(list!=null && list.size()>0){
            for (final BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1://私聊
                        if (item.getConversationId().equals(item.getConversationTitle())){
                            String id = item.getConversationId();
                            BmobQuery<User> query = new BmobQuery<>();
                            query.addWhereEqualTo("objectId",id);
                            query.findObjects(new FindListener<User>() {
                                @Override
                                public void done(List<User> list, BmobException e) {
                                    if (e==null&&list.size()>0){
                                        item.setConversationTitle(list.get(0).getNickname());
                                        item.setConversationIcon(list.get(0).getAvatar().getFileUrl());
                                        conversationList.add(item);
                                    }
                                }
                            });
                        }else {
                            conversationList.add(item);
                        }
                        Log.d("SessionFragment","会话信息： id:"+item.getConversationId()
                                +" title: "+item.getConversationTitle()+ " icon:"+item.getConversationIcon());
                        break;
                    default:
                        break;
                }
            }
        }
        SessionAdapter sessionAdapter = new SessionAdapter(getContext(),R.layout.listitem_friends,conversationList);
        listView.setAdapter(sessionAdapter);
    }
}
