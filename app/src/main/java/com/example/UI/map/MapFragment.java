package com.example.UI.map;

import static com.example.util.Code.LOGIN_ERROR_NOUSER;
import static com.example.util.Code.LOGIN_ERROR_PASSWORD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.android_client.LoginActivity;
import com.example.android_client.R;
import com.example.entity.MapInfo;
import com.example.entity.ShareInfo;
import com.example.entity.UserLogin;
import com.example.util.Code;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MapFragment extends Fragment {


    private MapView mv;
    private com.baidu.mapapi.map.BaiduMap baiduMap;
    private static final String CUSTOM_FILE_NAME_CX = "map_style.sty";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mapView = inflater.inflate(R.layout.fragment_map, container, false);

        mv = new MapView(getContext(), new BaiduMapOptions());

        mv = (MapView) mapView.findViewById(R.id.map);
        String customStyleFilePath = getCustomStyleFilePath(getContext(), CUSTOM_FILE_NAME_CX);
        // 设置个性化地图样式文件的路径和加载方式
        mv.setMapCustomStylePath(customStyleFilePath);
        // 动态设置个性化地图样式是否生效
        mv.setMapCustomStyleEnable(true);

        baiduMap = mv.getMap();

        /**
         * @param savedInstanceState:
         * @return void
         * @author Lee
         * @description 百度地图的单击事件
         * @date 2023/12/1 15:09
         */
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             * @param point 用户点击地理坐标
             */
            @Override
            public void onMapClick(LatLng point) {
                double latitude = point.latitude;  // 纬度
                double longitude = point.longitude;  // 经度

//                Toast.makeText(MapActivity.this, "纬度" + latitude + ";" + "经度" + longitude, Toast.LENGTH_SHORT).show();

                getLocationFromLatLng(latitude, longitude);

            }

            /**
             * 地图内 Poi 单击事件回调函数
             * @param mapPoi 点击的 poi 信息
             */
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });



        return mapView;
    }

    /**
     * @param context:
     * @param customStyleFileName:
     * @return String
     * @author zhang
     * @description 用于获取自定义地图模板sty文件的路径
     * @date 2023/12/2 11:45
     */
    private String getCustomStyleFilePath(Context context, String customStyleFileName) {
        FileOutputStream outputStream;
        outputStream = null;
        InputStream inputStream = null;
        String parentPath = null;
        try {
            inputStream = context.getAssets().open(customStyleFileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            parentPath = context.getFilesDir().getAbsolutePath();
            File customStyleFile = new File(parentPath + "/" + customStyleFileName);
            if (customStyleFile.exists()) {
                customStyleFile.delete();
            }
            customStyleFile.createNewFile();

            outputStream = new FileOutputStream(customStyleFile);
            outputStream.write(buffer);
        } catch (Exception e) {
            Log.e("CustomMapDemo", "Copy custom style file failed", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                Log.e("CustomMapDemo", "Close stream failed", e);
                return null;
            }
        }
        return parentPath + "/" + customStyleFileName;
    }

    /**
     * @param latitude:
     * @param longitude:
     * @return void
     * @author Lee
     * @description 逆编码方法，经纬度转地理位置，并把经纬度，地理位置一起传到backLocation方法处理
     * @date 2023/12/1 16:18
     */
    public void getLocationFromLatLng(double latitude, double longitude) {
        // 创建一个GeoCoder实例
        GeoCoder geoCoder = GeoCoder.newInstance();

        // 设置逆地理编码查询结果的监听器
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                String address;
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有找到检索结果
                    address = null;
                }
                // 获取到逆地理编码结果
                address = result.getAddress();  // 获取地理位置的地址信息
                String city = address.substring(0, address.indexOf("市")+1);  // 获取地理位置所属的城市
                // 在这里可以处理获取到的地理位置信息
                System.out.println("Address: " + address);
                System.out.println("City: " + city);

                interactiveServer(address.substring(0, address.indexOf("市")+1), latitude, longitude);//传递
                Toast.makeText(getContext(), "address" + address + ";" + "city" + city, Toast.LENGTH_SHORT).show();
            }

            public void onGetGeoCodeResult(GeoCodeResult result) {
                // 不需要处理正地理编码的结果，可以忽略
            }
        });

        // 创建一个经纬度对象
        LatLng latLng = new LatLng(latitude, longitude);
        // 发起逆地理编码请求
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        // 释放资源
        geoCoder.destroy();
    }

    /**
     * @param address:
     * @param latitude:
     * @param longitude:
     * @return void
     * @author Lee
     * @description 拿地理信息和经纬度和后端交互
     * @date 2023/12/7 9:07
     */
    public void interactiveServer(String address, double latitude, double longitude) {
        BigDecimal latitudeBigDecimal = new BigDecimal(latitude);
        BigDecimal longitudeBigDecimal = new BigDecimal(longitude);
        MapInfo mapInfo = new MapInfo();
        mapInfo.setLatitude(latitudeBigDecimal);
        mapInfo.setLongitude(longitudeBigDecimal);
        mapInfo.setPlaceName(address);

        Gson gson = new Gson();;
        String formBody = gson.toJson(mapInfo);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, formBody);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.7.88.235:8080/map/video")
                        .post(body)
                        .header("Authorization", TokenManager.getToken(getContext()))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        //失败
                        Log.e("MapInfo request failed", e.getMessage());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String shareInfo = response.body().string().trim();
                        Result result = gson.fromJson(shareInfo, Result.class);

                        if (!result.getFlag()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                    switch (result.getCode()) {
                                        case Code.TOKEN_NOT_EXIST:
                                        case Code.TOKEN_INVALID:
                                            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            startActivity(intent);
                                            break;
                                        default:
                                            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }

                        String data =  gson.toJson(result.getData());
                        System.out.println("data:"+data);
                        TypeToken<List<ShareInfo>> shareInfoTypeToken = new TypeToken<List<ShareInfo>>() {
                        };
                        List<ShareInfo> shareInfos = gson.fromJson(data, shareInfoTypeToken.getType());
                        //解析数据
                        List<String> urls = new ArrayList<>();
                        for (ShareInfo shareInfo1 : shareInfos) {
                            urls.add(shareInfo1.getVideoUrl());
                        }
                        Log.e("urls", urls.toString());

                        JumpToVedio(urls.toArray(new String[urls.size()]));
                    }
                });
            }
        }).start();
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 跳转至视频播放页面
     * @date 2023/12/7 9:43
     */
    private void JumpToVedio(String[] urls) {
        pauseSeconds();
        //跳转到Map_VideoActivity
        Activity activity = getActivity();
        Intent intent = new Intent(activity, Map_VideoActivity.class);
        // 在 Intent 中携带需要传递的数据
        intent.putExtra("urls", urls);
        activity.startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mv.onResume();
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 暂停1秒
     * @date 2023/12/5 18:33
     */
    private void pauseSeconds() {
        try {
            Thread.sleep(1000); // 3000毫秒即3秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
