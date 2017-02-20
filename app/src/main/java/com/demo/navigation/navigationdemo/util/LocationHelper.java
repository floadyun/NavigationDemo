package com.demo.navigation.navigationdemo.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by yixiaofei on 2017/2/17 0017.
 */
public class LocationHelper implements AMapLocationListener {

    public interface OnLocationListener{
         void locationSuccess(AMapLocation aMapLocation);
         void locationFailed(AMapLocation aMapLocation);
    }

    private static LocationHelper locationHelper;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private Context context;

    private OnLocationListener locationListener;

    public LocationHelper(Context context){
        this.context = context.getApplicationContext();
    }
    /**
     * 初始化定位设置
     */
    private void initLocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //定位一次
        mLocationOption.setOnceLocation(true);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 设置单次定位还是重复定位
     */
    public void setSingleLocation(boolean isSingle){
        if(isSingle){
            mLocationOption.setOnceLocation(isSingle);
        }
    }
    public static synchronized LocationHelper sharedInstance(Context context){
        if(locationHelper==null){
            locationHelper = new LocationHelper(context);
        }
        return locationHelper;
    }
    /**
     * 设置位置变化监听
     * @param locationListener
     */
    public void setOnLocationListener(OnLocationListener locationListener){
        this.locationListener = locationListener;
    }
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                showToastMessage("定位成功,当前位置为："+aMapLocation.getProvince()+aMapLocation.getCity()+aMapLocation.getDistrict()+aMapLocation.getStreet());
                if(this.locationListener!=null){
                    this.locationListener.locationSuccess(aMapLocation);
                }
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                showToastMessage("定位失败："+aMapLocation.getErrorInfo());
                if(locationListener!=null){
                    locationListener.locationFailed(aMapLocation);
                }
            }
        }
    }
    /**
     * 开始定位
     */
    public void startLocation(){
        initLocation();
        showToastMessage("开始定位");
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation(){
        showToastMessage("停止定位");
        if(mLocationClient!=null){
            mLocationClient.stopLocation();
        }
    }
    /**
     * 销毁定位客户端，同时销毁本地定位服务。
     */
    public void destroyLocation(){
        stopLocation();
        mLocationClient.onDestroy();

        mLocationOption = null;
        mLocationClient = null;
    }
    /**
     * 显示Toast信息
     * @param message
     */
    private void showToastMessage(String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
