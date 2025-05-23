package com.example.UI.map;

import static com.example.android_client.LoginActivity.ip;
import static com.example.util.Code.LOGIN_ERROR_NOUSER;
import static com.example.util.Code.LOGIN_ERROR_PASSWORD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.UI.camera.CameraFragment;
import com.example.UI.camera.PreviewVideoActivity;
import com.example.android_client.EnrollActivity;
import com.example.android_client.LoginActivity;
import com.example.android_client.MainActivity;
import com.example.android_client.R;
import com.example.entity.MapInfo;
import com.example.entity.ShareInfo;
import com.example.entity.UserLogin;
import com.example.util.Code;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
//import com.nightonke.boommenu.BoomButtons.HamButton;
//import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
//import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
//import com.nightonke.boommenu.BoomMenuButton;
//import com.nightonke.boommenu.ButtonEnum;
//import com.nightonke.boommenu.Piece.PiecePlaceEnum;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private static final String CUSTOM_FILE_NAME_CX_NORMAL = "map_style.sty";
    private static final String CUSTOM_FILE_NAME_CX_NIGHT = "map_style_blue.sty";
    private static final String CUSTOM_FILE_NAME_CX_GREEN = "map_style_green.sty";

//    private BoomMenuButton button;

    private View contextView;

    private Button btnNormalMap;
    private Button btnSatelliteMap;
    private Button btnTraceMap;
    private List<Overlay> mOverlayList = new ArrayList<>();
    private DistrictSearch mDistrictSearch;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mapView = inflater.inflate(R.layout.fragment_map, container, false);


        // 对爆炸菜单进行初始化

//        button = mapView.findViewById(R.id.bmb);
//        button.setButtonEnum(ButtonEnum.TextInsideCircle);
//        button.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
//        button.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_1);
//        button.setHighlightedColor(R.color.m3_button_ripple_color_selector);
//        button.setDraggable(true);

        mv = new MapView(getContext(), new BaiduMapOptions());

        mv = (MapView) mapView.findViewById(R.id.map);
        contextView = mapView.findViewById(R.id.context_view);
        btnNormalMap = mapView.findViewById(R.id.btn_normal_map);
        btnSatelliteMap = mapView.findViewById(R.id.btn_satellite_map);
        btnTraceMap = mapView.findViewById(R.id.btn_trace_map);
        String customStyleFilePath = getCustomStyleFilePath(getContext(), CUSTOM_FILE_NAME_CX_NORMAL);
        // 设置个性化地图样式文件的路径和加载方式
        mv.setMapCustomStylePath(customStyleFilePath);
        // 动态设置个性化地图样式是否生效
        mv.setMapCustomStyleEnable(true);

        baiduMap = mv.getMap();

        // 初始化爆炸菜单构建器
//        setBuilderForBomMenu(mv);

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
     * @param :
     * @return void
     * @author zhang
     * @description 初始化爆炸菜单的菜单项
     * @date 2023/12/9 17:55
     */
//    private void setBuilderForBomMenu(MapView mv) {
//        TextInsideCircleButton.Builder builder0 = new TextInsideCircleButton.Builder()
//                .normalImageRes(R.mipmap.cat)
//                .normalTextRes(R.string.map_style_night)
//                .pieceColor(R.color.black)
//                .listener(new OnBMClickListener() {
//                    @Override
//                    public void onBoomButtonClick(int index) {
//                        String customStyleFilePath = getCustomStyleFilePath(getContext(), CUSTOM_FILE_NAME_CX_NIGHT);
//                        // 设置个性化地图样式文件的路径和加载方式
//                        mv.setMapCustomStylePath(customStyleFilePath);
//                        // 动态设置个性化地图样式是否生效
//                        mv.setMapCustomStyleEnable(true);
//
//                        baiduMap = mv.getMap();
//                    }
//                });
//        button.addBuilder(builder0);
//
//        TextInsideCircleButton.Builder builder1 = new TextInsideCircleButton.Builder()
//                .normalImageRes(R.mipmap.dolphin)
//                .normalTextRes(R.string.map_style_green)
//                .pieceColor(R.color.black)
//                .listener(new OnBMClickListener() {
//                    @Override
//                    public void onBoomButtonClick(int index) {
//                        String customStyleFilePath = getCustomStyleFilePath(getContext(), CUSTOM_FILE_NAME_CX_GREEN);
//                        // 设置个性化地图样式文件的路径和加载方式
//                        mv.setMapCustomStylePath(customStyleFilePath);
//                        // 动态设置个性化地图样式是否生效
//                        mv.setMapCustomStyleEnable(true);
//
//                        baiduMap = mv.getMap();
//                    }
//                });
//        button.addBuilder(builder1);
//
//        TextInsideCircleButton.Builder builder2 = new TextInsideCircleButton.Builder()
//                .normalImageRes(R.mipmap.bat)
//                .normalTextRes(R.string.map_style_normal)
//                .pieceColor(R.color.black)
//                .listener(new OnBMClickListener() {
//                    @Override
//                    public void onBoomButtonClick(int index) {
//                        String customStyleFilePath = getCustomStyleFilePath(getContext(), CUSTOM_FILE_NAME_CX_NORMAL);
//                        // 设置个性化地图样式文件的路径和加载方式
//                        mv.setMapCustomStylePath(customStyleFilePath);
//                        // 动态设置个性化地图样式是否生效
//                        mv.setMapCustomStyleEnable(true);
//
//                        baiduMap = mv.getMap();
//                    }
//                });
//        button.addBuilder(builder2);
//
//        //地图上三个按钮的点击事件
//        setListener();
//        // 初始化DistrictSearch
//        mDistrictSearch = DistrictSearch.newInstance();
//        mDistrictSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
//            @Override
//            public void onGetDistrictResult(DistrictResult districtResult) {
//                if (districtResult.error == DistrictResult.ERRORNO.NO_ERROR) {
//                    // 获取对应省份的边界点的经纬度的集合
//                    List<LatLng> boundaryPoints = districtResult.getPolylines().get(0);
//                    // 在这里可以处理获取到的边界点数据
//                    colorIn(boundaryPoints,generateRandomColor());//将该区域上色
//                }
//            }
//        });
//
//    }

    /**
     * @param boundaryPoints:
     * @return void
     * @author Lee
     * @description 区域填色
     * @date 2023/12/22 16:59
     */
    private void colorIn(List<LatLng> boundaryPoints,int fillColor) {
        PolygonOptions mPolygonOptions = new PolygonOptions()
                .points(boundaryPoints)
                .fillColor(fillColor) //填充颜色
                .stroke(new Stroke(5, generateRandomColor())); //边框宽度和颜色

        //在地图上显示多边形
        baiduMap.addOverlay(mPolygonOptions);
    }

    private void setListener() {
        //切换为普通地图
        btnNormalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                baiduMap.clear();
            }
        });
        //切换为卫星地图
        btnSatelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                baiduMap.clear();
            }
        });
        //切换为轨迹地图
        btnTraceMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//                DistrictSearchOption option = new DistrictSearchOption();
//                option.cityName("北京市"); // 设置要查询的省份名称
//                mDistrictSearch.searchDistrict(option);
//
//                DistrictSearchOption option2 = new DistrictSearchOption();
//                option2.cityName("石家庄市");
//                mDistrictSearch.searchDistrict(option2);

//                DistrictSearchOption option3 = new DistrictSearchOption();
//                option3.cityName("四川省");
//                mDistrictSearch.searchDistrict(option3);

                getPlaceName(TokenManager.getUserName(getContext()));
            }
        });
    }

    /**
     * @param userName:
     * @return void
     * @author zhang
     * @description 发送请求获取打卡地点名
     * @date 2023/12/31 14:38
     */
    private void getPlaceName(String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://"+ip+":8080/map/getPlaceName?username=" + userName)
                        .get()
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "服务器连接错误，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        Gson gson = new Gson();
                        String res = response.body().string();
                        Result result = gson.fromJson(res, Result.class);
                        if (!result.getFlag()) {
                            Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String data = result.getData().toString();
                        TypeToken<List<String>> typeToken = new TypeToken<List<String>>() {
                        };
                        List<String> placeNameList = gson.fromJson(data, typeToken.getType());

                        // 清除之前的覆盖物
                        baiduMap.clear();

//                        for (String placeName : placeNameList) {
//                            Log.e("placeName", placeName);
//                            DistrictSearchOption option = new DistrictSearchOption();
//                            option.cityName(placeName.substring(placeName.indexOf('省') + 1, placeName.indexOf('市'))); // 设置要查询的省份名称
//                            mDistrictSearch.searchDistrict(option);
//                        }
                        System.out.println(placeNameList);

//                        // 开始逐个处理地点
                        processPlaceNames(placeNameList);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "渲染打卡地点成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        }).start();
    }

    private void processPlaceNames(List<String> placeNameList) {
        // 如果 placeNameList 为空，直接返回
        if (placeNameList == null || placeNameList.isEmpty()) {
            return;
        }

        // 在处理第一个地点名称之前清除地图
        baiduMap.clear();

        // 开始逐个处理地点
        processPlaceName(placeNameList, 0);
    }

    private void processPlaceName(List<String> placeNameList, int index) {
        if (index >= placeNameList.size()) {
            // 已处理完所有地点
            return;
        }

        String placeName = placeNameList.get(index);
        Log.e("placeName", placeName);

        DistrictSearchOption option = new DistrictSearchOption();
        option.cityName(placeName.substring(placeName.indexOf('省') + 1, placeName.indexOf('市')+1));
        System.out.println(placeName.substring(placeName.indexOf('省') + 1, placeName.indexOf('市')+1));

        // 在 DistrictSearch 异步执行完毕后再处理下一个地点
        mDistrictSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
            @Override
            public void onGetDistrictResult(DistrictResult districtResult) {
                if (districtResult.error == DistrictResult.ERRORNO.NO_ERROR) {
                    // 获取对应省份的边界点的经纬度的集合
                    List<LatLng> boundaryPoints = districtResult.getPolylines().get(0);
                    // 在这里可以处理获取到的边界点数据
                    colorIn(boundaryPoints,generateRandomColor());//将该区域上色
                }

                // 处理下一个地点
                processPlaceName(placeNameList, index + 1);
            }
        });

        mDistrictSearch.searchDistrict(option);
    }

    // 动态生成颜色
    private int generateRandomColor() {
        Random random = new Random();
        return Color.argb(188,random.nextInt(256), random.nextInt(256), random.nextInt(256));
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
        Intent intent = new Intent(getContext(), PreviewVideoActivity.class);
        startActivity(intent);
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
                String city = null;
                if(address != null) {
                    city = address.substring(0, address.indexOf("市")+1);  // 获取地理位置所属的城市
                }
                // 在这里可以处理获取到的地理位置信息
                System.out.println("Address: " + address);
                System.out.println("City: " + city);
                System.out.println("latitude:" + latitude);
                System.out.println("longitude:"+longitude);

                interactiveServer(address.substring(0, address.indexOf("市")+1), latitude, longitude);//传递
                showSnackBar(contextView,"address" + address + ";" + "city" + city,"我知道了");
//                Toast.makeText(getContext(), "address" + address + ";" + "city" + city, Toast.LENGTH_SHORT).show();
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
                        .url("http://"+ip+":8080/map/video")
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
                                showSnackBar(contextView,"请求失败","我知道了");
//                                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
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
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);

//                                    Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                    showSnackBar(contextView,result.getMsg(),"我知道了");
                                    switch (result.getCode()) {
                                        case Code.TOKEN_NOT_EXIST:
                                        case Code.TOKEN_INVALID:
                                            //                                            showSnackBar(contextView,result.getMsg(),"我知道了");
//                                            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                            showToast(getActivity(),result.getMsg());
                                            startActivity(intent);
                                            break;
                                        case Code.TOKEN_OTHER_LOGIN:
//                                            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                            showToast(getActivity(),result.getMsg());
                                            TokenManager.deleteExpiredTokenFromSharedPreferences(getContext());
                                            startActivity(intent);
                                            break;
                                        default:
                                            showSnackBar(contextView,result.getMsg(),"我知道了");
//                                            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_SHORT).show();
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
                        List<String> videoUrls = new ArrayList<>();
                        List<String> vrUrls = new ArrayList<>();
                        ArrayList<Integer> videoIds = new ArrayList<>();
                        ArrayList<Integer> userInfoIds = new ArrayList<>();
                        for (ShareInfo shareInfo1 : shareInfos) {
                            videoUrls.add(shareInfo1.getVideoUrl());
                            vrUrls.add(shareInfo1.getVrImageUrl());
                            videoIds.add(shareInfo1.getId());
                            userInfoIds.add(shareInfo1.getUserInfoId());
                        }
                        Log.e("videoUrls", videoUrls.toString());
                        Log.e("vrUrls", vrUrls.toString());
                        Log.e("videoIds", videoIds.toString());

                        Integer[] integers = videoIds.toArray(new Integer[videoIds.size()]);
                        JumpToVedio(videoUrls.toArray(new String[videoUrls.size()]),vrUrls.toArray(new String[vrUrls.size()]),videoIds,userInfoIds);
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
    private void JumpToVedio(String[] videoUrls,String[] vrUrls,ArrayList<Integer> videoIds,ArrayList<Integer> userInfoIds) {
        pauseSeconds();
        //跳转到Map_VideoActivity
        Activity activity = getActivity();
        Intent intent = new Intent(activity, Map_VideoActivity.class);
        // 在 Intent 中携带需要传递的数据
        intent.putExtra("videoUrls", videoUrls);
        intent.putExtra("vrUrls", vrUrls);
        intent.putIntegerArrayListExtra("videoIds", videoIds);
        intent.putIntegerArrayListExtra("userInfoIds", userInfoIds);
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
    public void showSnackBar(View view,String txt,String btnTxt){
        Snackbar snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG);
        snackbar.setAction(btnTxt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理撤销逻辑
            }
        });
        snackbar.show();

    }
    public void showToast(Context context, String string){
        Toast toast = new Toast(context);
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        //设置Toast显示位置，居中，向X,Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER,0,height/3);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast,null);
        TextView tvMessage = (TextView) view.findViewById(R.id.ErrorTips);
        //设置文本
        tvMessage.setText(string);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();

    }

    private WindowManager getWindowManager() {
        Context context = requireContext();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager;
    }

}
