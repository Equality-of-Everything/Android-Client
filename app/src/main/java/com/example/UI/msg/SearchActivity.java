package com.example.UI.msg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.adapter.SearchAdapter;
import com.example.android_client.R;
import com.example.util.TokenManager;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMUserInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private SearchBar searchBar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private List<String> userNameList = new ArrayList<>();
    private List<EMContact> contactsList;
    private Set<String> usernameSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        contactsList = new ArrayList<>();
//        userNameList = TokenManager.getEMUserName(this);
        usernameSet = TokenManager.getEMUserName(this);
        for (String userName : usernameSet) {
            userNameList.add(userName);
        }
        Log.e("SearchActivity", "userNameList.size : " + userNameList.size());

        // 初始化contactsList
        initializeContactsList();

        adapter = new SearchAdapter(contactsList, this, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setListener();
        Log.e("SearchActivity", "userNameList.size : " + userNameList.size());

    }



    /**
     * @param :
     * @return void
     * @author Lee
     * @description 初始化contactsList
     * @date 2023/12/27 10:03
     */
    private void initializeContactsList() {
        // 从环信服务器获取所有的用户信息
        for (final String userName : userNameList) {
            EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(new String[]{userName}, new EMValueCallBack<Map<String, EMUserInfo>>() {
                @Override
                public void onSuccess(Map<String, EMUserInfo> value) {
                    EMUserInfo userInfo = value.get(userName);
                    if (userInfo != null) {
                        EMContact contact = new EMContact(userInfo.getUserId().toLowerCase());

                        contactsList.add(contact);
                        Log.e("SearchActivity", "contactsList : " + contactsList.size());
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {
                    // 处理获取用户信息失败的情况
                    Log.e("SearchActivity", "Error fetching user info: " + errorMsg);
                }
            });
        }
    }

    private void setListener() {

//        searchBar.setOnMenuItemClickListener(menuItem -> {
//            performSearch(searchView.getText().toString());
//            Log.e("SearchActivity", "searchView.getText().toString()" + searchView.getText().toString());
//            return true;
//        });
        searchView.getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            searchBar.setText(searchView.getText());
//                            searchView.hide();
                            performSearch(searchView.getText().toString());
                            Log.e("SearchActivity", "searchView.getText().toString()" + searchView.getText().toString());
                            return false;
                        });

    }

    /**
     * @param searchText:
     * @return void
     * @author Lee
     * @description 模糊查找
     * @date 2023/12/23 21:20
     */
    private void performSearch(String searchText) {
        searchText = searchText.trim(); // Trim the search text
        List<EMContact> searchResults = new ArrayList<>();
        for (EMContact contact : contactsList) {
            String username = contact.getUsername().toLowerCase().trim(); // Trim the username
            Log.e("SearchActivity", "username : " + username);
            Log.e("SearchActivity", "searchText : " + searchText);
            if (username.contains(searchText)) {
                searchResults.add(contact);
            }
        }
        adapter.setContactList(searchResults); // Update the adapter with the search results
        Log.e("SearchActivity", "searchResults : " + searchResults.size());
    }

    private void initView() {
        searchBar = findViewById(R.id.search_icon);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.rv_search);
    }
}