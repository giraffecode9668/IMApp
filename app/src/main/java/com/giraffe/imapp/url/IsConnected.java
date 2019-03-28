package com.giraffe.imapp.url;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.giraffe.imapp.pojo.ConFriend;

import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;

import static cn.bmob.newim.core.BmobIMClient.getContext;

public class IsConnected {

    /* **************** */
    /* 判断网络是否连接 */
    /* **************** */
    public static boolean isNetworkConnected(Context context) {
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
