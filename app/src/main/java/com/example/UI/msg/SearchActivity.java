package com.example.UI.msg;

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
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private SearchBar searchBar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private List<EMContact> contactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();

        adapter = new SearchAdapter(contactsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setListener();
        Log.e("SearchActivity", "onCreate executed");
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
                            searchView.hide();
                            performSearch(searchView.getText().toString());
                            Log.e("SearchActivity", "searchView.getText().toString()" + searchView.getText().toString());
                            return false;
                        });

    }

    /**
     * @param searchText:
     * @return void
     * @author Lee
     * @description 精确查找
     * @date 2023/12/23 21:20
     */
    private void performSearch(String searchText) {
        List<EMContact> searchResults = new ArrayList<>();
        for (EMContact contact : contactsList) {
            if (contact.getUsername().toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(contact);
            }
        }
        adapter.setContactList(searchResults); // 更新适配器的数据源
        adapter.notifyDataSetChanged(); // 刷新界面
    }

    private void initView() {
        searchBar = findViewById(R.id.search_icon);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.rv_search);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //更新到主界面
        adapter.setContactList(contactsList);
        Log.e("SearchActivity", "searchResultList.size() = " + contactsList.size());
        adapter.notifyDataSetChanged();
    }
}