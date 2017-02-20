package com.demo.navigation.navigationdemo.gaode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.demo.navigation.navigationdemo.R;
import com.demo.navigation.navigationdemo.bean.PositionBean;
import com.demo.navigation.navigationdemo.overlay.PoiOverlay;
import com.demo.navigation.navigationdemo.util.LocationHelper;
import com.demo.navigation.navigationdemo.util.POISearchHelper;
import com.demo.navigation.navigationdemo.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class Gaode3DMap extends AppCompatActivity implements LocationSource,LocationHelper.OnLocationListener,
        POISearchHelper.OnPoiSearchFinished,TextWatcher,Inputtips.InputtipsListener,AdapterView.OnItemClickListener,
        AMap.OnMarkerClickListener,AMap.InfoWindowAdapter,AMap.OnInfoWindowClickListener,AMap.OnMapClickListener{
    @BindView(R.id.map_3d_view)
    MapView mapView;

    @BindView(R.id.search_auto_text)
    AutoCompleteTextView searchText;

    @BindView(R.id.input_list)
    ListView inputList;

    private AMap aMap;

    private OnLocationChangedListener locationChangedListener;

    private PositionBean positionBean;

    private List<HashMap<String, String>> searchList;

    private SimpleAdapter aAdapter;

    private PoiOverlay mPoiOverlay;

    private Marker mLocationMarker;

    private Circle mLocationCircle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_3d_layout);
        ButterKnife.bind(this);

        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
        //设置地图缩放
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        LocationHelper.sharedInstance(this).setOnLocationListener(this);
        POISearchHelper.sharedInstance(this).setOnPoiSearchFinished(this);
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        aMap.setOnMarkerClickListener(this);
//        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapClickListener(this);

        searchText.addTextChangedListener(this);
        inputList.setOnItemClickListener(this);
    }
    public void searchAround(View view){
        if(positionBean!=null){
            POISearchHelper.sharedInstance(this).startSearch(searchText.getText().toString(),"",positionBean.getCityCode(),10);
        }
    }

    /**
     * 按钮点击事件
     * @param view
     */
    public void buttonClick(View view){
        switch (view.getId()){
            case R.id.common_map_btn:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 标准地图模式
                break;
            case R.id.satellite_map_btn:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                break;
            case R.id.night_map_btn:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);// 夜间地图模式
                break;
        }
    }
    /**
     * 开始定位
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
         LocationHelper.sharedInstance(this).startLocation();
         LocationHelper.sharedInstance(this).setSingleLocation(true);
         locationChangedListener = onLocationChangedListener;
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        LocationHelper.sharedInstance(this).destroyLocation();
    }
    @Override
    public void locationSuccess(AMapLocation aMapLocation) {
        System.out.println("定位成功..."+aMapLocation.getCity());
        locationChangedListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
        LatLng curLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//        if (mLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(curLatLng);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
            mLocationMarker = aMap.addMarker(markerOptions);
//        }
//        if (mLocationCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(curLatLng);
            circleOptions.radius(aMapLocation.getAccuracy());
            circleOptions.strokeWidth(2);
            circleOptions.strokeColor(getResources().getColor(R.color.stroke));
            circleOptions.fillColor(getResources().getColor(R.color.fill));
            mLocationCircle = aMap.addCircle(circleOptions);
//        }
        positionBean = new PositionBean();
        positionBean.setCity(aMapLocation.getCity());
        positionBean.setCityCode(aMapLocation.getCityCode());
        positionBean.setPostionX(aMapLocation.getLatitude());
        positionBean.setPostionY(aMapLocation.getLongitude());

        POISearchHelper.sharedInstance(this).searchAroud(aMapLocation.getLatitude(),aMapLocation.getLongitude(),10000);
    }
    @Override
    public void locationFailed(AMapLocation aMapLocation) {

    }
    @Override
    public void searchSuccessed(PoiResult poiResult) {
        if (poiResult != null) {
            if (mPoiOverlay != null) {
                mPoiOverlay.removeFromMap();
            }
                // 取得搜索到的poiitems有多少页
                List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                if (poiItems != null && poiItems.size() > 0) {
                    aMap.clear();// 清理之前的图标
                    mPoiOverlay = new PoiOverlay(aMap, poiItems);
                    mPoiOverlay.removeFromMap();
                    mPoiOverlay.addToMap();
                    mPoiOverlay.zoomToSpan();
                }
        }
    }
    @Override
    public void searchFailed(PoiResult poiResult) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    boolean isSetList = true;
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(positionBean!=null&&isSetList){
            isSetList = true;
            System.out.println("the city is "+positionBean.getCity());
            String newText = charSequence.toString().trim();
            InputtipsQuery inputquery = new InputtipsQuery(newText, positionBean.getCity());
            inputquery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(Gaode3DMap.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
    @Override
    public void onGetInputtips(List<Tip> tipList, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            searchList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < tipList.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", tipList.get(i).getName());
                map.put("address", tipList.get(i).getDistrict());
                searchList.add(map);
            }
            aAdapter = new SimpleAdapter(getApplicationContext(), searchList, R.layout.item_layout,
                    new String[] {"name","address"}, new int[] {R.id.poi_field_id, R.id.poi_value_id});

            inputList.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
           // ToastUtil.showerror(this.getApplicationContext(), rCode);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView)view.findViewById(R.id.poi_field_id);
        searchText.setText(textView.getText().toString());
        isSetList = false;
        inputList.setAdapter(null);
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        LocationHelper.sharedInstance(this).destroyLocation();
    }
    /**
     * 显示Toast信息
     * @param message
     */
    private void showToastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        int index = mPoiOverlay.getPoiIndex(marker);
        float distance = mPoiOverlay.getDistance(index);
        String showDistance = Utils.getFriendlyDistance((int) distance);
        snippet.setText("距当前位置" + showDistance);
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        // 调起导航
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("开始导航...");
                mLocationMarker.hideInfoWindow();
                startAMapNavi(marker);
            }
        });
        return view;
    }
    /**
     * 点击一键导航按钮跳转到导航页面
     *
     * @param marker
     */
    private void startAMapNavi(Marker marker) {
        if (positionBean == null) {
            return;
        }
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("gps", false);
        intent.putExtra("start", new NaviLatLng(positionBean.getPostionX(), positionBean.getPostionY()));
        intent.putExtra("end", new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        startActivity(intent);
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("onInfoWindowClick...");
        mLocationMarker.hideInfoWindow();
        startAMapNavi(marker);
    }
    @Override
    public void onMapClick(LatLng latLng) {
        System.out.println("onMapClick...");
        if (mLocationMarker != null) {
            System.out.println("hideInfoWindow...");
            mLocationMarker.hideInfoWindow();
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        System.out.println("您点击了mark...");
        if (mLocationMarker == marker) {
            return false;
        }
        return false;
    }
}
