package com.example.UI.mine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.TextUtils;

import com.example.adapter.FriendAdapter;
import com.example.android_client.R;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMOptions;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    private List<EMContact> contacts = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = findViewById(R.id.rv_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendAdapter = new FriendAdapter(contacts);
        recyclerView.setAdapter(friendAdapter);

        // 手动触发数据加载和显示
        loadDataAndDisplay();

        // 下拉刷新监听，下拉加载更多好友
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 重新获取好友列表
                contacts.clear(); // 清空原有数据
                doAsyncFetchAllContactsFromServer("", 20);
                swipeRefreshLayout.setRefreshing(false); // 刷新完成
            }

        });
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 手动触发一次获取好友，跳转过来可以直接显示好友
     * @date 2023/12/12 10:28
     */
    private void loadDataAndDisplay() {
        String cursor = ""; // 初始游标
        int limit = 20; // 单次获取的数据条数
        doAsyncFetchAllContactsFromServer(cursor, limit);
    }

    /**
     * @param cursor:
     * @param limit:
     * @return void
     * @author Lee
     * @description 异步获取好友，一次最多获取20个
     * @date 2023/12/12 10:28
     */
    private void doAsyncFetchAllContactsFromServer(String cursor, int limit) {
        EMClient.getInstance().contactManager().asyncFetchAllContactsFromServer(limit, cursor, new EMValueCallBack<EMCursorResult<EMContact>>() {
            @Override
            public void onSuccess(EMCursorResult<EMContact> value) {
                List<EMContact> data = value.getData();
                String resultCursor = value.getCursor();
                if (!CollectionUtils.isEmpty(data)) {
                    contacts.addAll(data); // 使用FriendsActivity的成员变量contacts
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendAdapter.notifyDataSetChanged(); // 刷新RecyclerView显示
                        }
                    });
                }
                if (!TextUtils.isEmpty(resultCursor)) {
                    // 继续获取更多好友
                    doAsyncFetchAllContactsFromServer(resultCursor, limit);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                // 处理错误情况
            }
        });
    }


}










