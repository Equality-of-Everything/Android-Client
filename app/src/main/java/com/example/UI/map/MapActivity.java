package com.example.UI.map;

import android.app.Activity;
import android.os.Bundle;
import com.baidu.mapapi.map.MapView;
import com.example.android_client.R;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/11/30 19:16
 * @Decription :
 */

public class MapActivity extends Activity {
    private MapView mv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mv = findViewById(R.id.map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.onResume();
    }
}
