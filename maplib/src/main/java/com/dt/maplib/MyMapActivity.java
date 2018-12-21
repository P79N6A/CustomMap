package com.dt.maplib;

import android.content.Context;
import android.graphics.Point;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

public class MyMapActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private AutoCompleteTextView searchText;
    private ListView listView;
    private SearchResultAdapter searchResultAdapter;
    private String city;
    private SuggestionSearch mSuggestionSearch;
    private List<LatInfoBean> autoTips = new ArrayList<>();
    private PoiSearch mPoiSearch;
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private ImageView imageView;
    private SegmentedGroup mSegmentedGroup;
    private String[] items = {"小区", "学校", "楼宇", "商场" };
    private String searchType = "小区";
    private LatLng mCenterLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();
        //获取地图控件引用

        initView();

        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        initLocationOption();

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

                imageView.setVisibility(View.VISIBLE);
                mBaiduMap.clear();
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                updateMapState(mapStatus);
                mSegmentedGroup.setVisibility(View.VISIBLE);

            }
        });
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

            }
        });


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                if (newText.length() > 0) {

                    Log.e("onTextChanged", "onTextChanged: " + newText + city);
                    if (city == null || city.length() == 0) {
                        city = "杭州市";
                    }

                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(newText)
                            .city(city)
                    );
                    mSegmentedGroup.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            private SuggestionResult.SuggestionInfo tip;
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (autoTips != null && autoTips.size() > position) {
//                    tip = autoTips.get(position);
//                    searchPoi(tip);
//                }
//            }
//        });

        adp = new MySearchResultAdapter(MyMapActivity.this);
        listView.setAdapter(adp);     // 绑定adapter

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LatInfoBean latInfoBean = autoTips.get(position);
                Toast.makeText(MyMapActivity.this, ""+latInfoBean.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        mSegmentedGroup = (SegmentedGroup) findViewById(R.id.segmented_group);
        mSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                searchType = items[0];
                if (checkedId == R.id.radio0) {
                    searchType = items[0];

                } else if (checkedId == R.id.radio1) {
                    searchType = items[1];

                } else if (checkedId == R.id.radio2) {
                    searchType = items[2];

                } else if (checkedId == R.id.radio3) {
                    searchType = items[3];

                }
                searchNeayBy();
            }
        });


    }


    private void updateMapState(MapStatus status) {

        imageView.setVisibility(View.GONE);
        mBaiduMap.clear();
         mCenterLatLng = status.target;

        /**获取经纬度*/
        double lat = mCenterLatLng.latitude;
        double lng = mCenterLatLng.longitude;
        BitmapDescriptor mapBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.pin);
        MarkerOptions option = new MarkerOptions().position(mCenterLatLng).icon(mapBitmap).zIndex(9).draggable(true);
        option.animateType(MarkerOptions.MarkerAnimateType.grow);
        mBaiduMap.addOverlay(option);
        Log.e("onTextChanged", "updateMapState: " + lat + "-" + lng);
        searchNeayBy();
    }

    /**
     * 搜索周边地理位置
     * by hankkin at:2015-11-01 22:54:49
     */
    private void searchNeayBy() {
//        PoiNearbySearchOption option = new PoiNearbySearchOption();
//        option.keyword(searchType);
//        option.sortType(PoiSortType.distance_from_near_to_far);
//        option.location(mCenterLatLng);
//        option.radius(1000);
//        option.pageCapacity(20);
//        mPoiSearch.searchNearby(option);

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .keyword(searchType)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(mCenterLatLng)
                .radius(1000)
                .pageCapacity(20));
    }


    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private void searchPoi(SuggestionResult.SuggestionInfo tip) {

        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(tip.getKey())
                .pageNum(10));

    }

    private void initView() {

        imageView = (ImageView) findViewById(R.id.image);

        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        mMapView = (MapView) findViewById(R.id.map);
        listView = (ListView) findViewById(R.id.listview);
        searchResultAdapter = new SearchResultAdapter(MyMapActivity.this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    private MySearchResultAdapter adp;
    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
        public void onGetSuggestionResult(SuggestionResult res) {

            if (res == null || res.getAllSuggestions() == null) {
                return;
                //未找到相关结果
            }
            List<LatInfoBean> list = new ArrayList<>();

            Log.e("onTextChanged", "onGetSuggestionResult: " + autoTips.size());
            for (int i = 0; i < res.getAllSuggestions().size(); i++) {
                SuggestionResult.SuggestionInfo suggestionInfo = res.getAllSuggestions().get(i);
                LatInfoBean latInfoBean = new LatInfoBean(suggestionInfo.getKey(),suggestionInfo.getDistrict(),suggestionInfo.getPt());
                list.add(latInfoBean);

            }

            autoTips.clear();
            autoTips.addAll(list);


            adp.setData(autoTips);

            adp.setSelectedPosition(0);
            adp.notifyDataSetChanged();
            //获取在线建议检索结果
        }
    };

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        private PoiInfo poiInfo;

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            Log.e("onTextChanged", "onGetPoiResult: " + poiResult.toString());
            //获取POI检索结果
            List<PoiAddrInfo> allAddr = poiResult.getAllAddr();
            List<PoiInfo> allPoi = poiResult.getAllPoi();
            if (allPoi != null) {
                List<LatInfoBean> list = new ArrayList<>();
                for (int i = 0; i < allPoi.size(); i++) {
                    Log.e("onTextChanged", "allPoi: " + allPoi.get(i).address + allPoi.get(i).name);
                    poiInfo = allPoi.get(i);
                    LatInfoBean latInfoBean = new LatInfoBean(poiInfo.getName(),poiInfo.getAddress(),poiInfo.getLocation());
                    list.add(latInfoBean);
                }
                autoTips.clear();
                autoTips.addAll(list);


                adp.setData(autoTips);

                adp.setSelectedPosition(0);
                adp.notifyDataSetChanged();


            }

            if (allAddr != null) {
                for (int i = 0; i < allAddr.size(); i++) {
                    Log.e("onTextChanged", "allAddr: " + allAddr.get(i).address + allAddr.get(i).name);
                }
            }


        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (poiDetailInfoList != null) {
                for (int i = 0; i < poiDetailInfoList.size(); i++) {
                    Log.e("onTextChanged", "poiDetailInfoList: " + poiDetailInfoList.get(i).getName() + poiDetailInfoList.get(i).getAddress());
                }
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    /**
     * 初始化定位参数配置
     */

    private void initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setOpenGps(true);              //打开GPS
        locationOption.setCoorType("bd09ll");        //设置坐标类型
        locationOption.setScanSpan(1000 * 60 * 3);            //设置发起定位请求的间隔时间为5000ms
        locationOption.setIsNeedAddress(true);

        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();

            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            //mapview 销毁后不在处理新接收的位置
            if (location == null || mBaiduMap == null) {
                return;
            }
            //MyLocationData.Builder定位数据建造器
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            //设置定位数据
            mBaiduMap.setMyLocationData(locData);

            //Toast.makeText(getApplicationContext(), String.valueOf(latitude), Toast.LENGTH_SHORT).show();
            //第一次定位的时候，那地图中心点显示为定位到的位置

            //地理坐标基本数据结构
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            //MapStatusUpdate描述地图将要发生的变化
            //MapStatusUpdateFactory生成地图将要反生的变化
            MapStatus status = new MapStatus.Builder().zoom(15).target(loc).build();
            MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(status);
            mBaiduMap.animateMapStatus(msu);
            //Toast.makeText(getApplicationContext(), location.getAddrStr(),
            // Toast.LENGTH_SHORT).show();
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            city = location.getCity();    //获取城市

            Log.e("onTextChanged", "onReceiveLocation: " + city);


            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        mBaiduMap.setMyLocationEnabled(false);
    }
}
