package com.demo.navigation.navigationdemo.gaode;

import android.content.Context;

import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

/**
 * Created by yixiaofei on 2017/2/20 0020.
 * 路径规划帮助类
 */

public class RouteSearchHelper implements RouteSearch.OnRouteSearchListener{
    //路径规划完成回调接口
    public interface OnRouteSearchFinished{
         void routeDriveFinished(DriveRouteResult routeResult,int code);
         void routeBusFinished(BusRouteResult routeResult,int code);
         void routeRideFinished(RideRouteResult routeResult,int code);
         void routeWalkFinished(WalkRouteResult routeResult,int code);
    }
    private static RouteSearchHelper routeSearchHelper;
    //驾车导航
    public static final int DRIVE_ROUTE = 1;
    //步行导航
    public static final int WALK_ROUTE = 2;
    //公交导航
    public static final int BUS_ROUTE = 3;
    //骑行导航
    public static final int RIDE_ROUTE = 4;

    private int navigationType = 0;

    private Context context;

    private RouteSearch routeSearch;

    private OnRouteSearchFinished onRouteSearchFinished;

    public RouteSearchHelper(Context context){
        this.context = context.getApplicationContext();
    }
    public RouteSearchHelper sharedInstance(Context context){
        if(routeSearchHelper==null){
            routeSearchHelper = new RouteSearchHelper(context);
        }
        return routeSearchHelper;
    }

    /**
     * 设置导航路径规划结束监听
     * @param onRouteSearchFinished
     */
    public void setOnRouteSearchFinished(OnRouteSearchFinished onRouteSearchFinished) {
        this.onRouteSearchFinished = onRouteSearchFinished;
    }

    /**
     * 初始化路径导航类
     */
    public void initRouteSearch(){
        routeSearch = new RouteSearch(context);

        routeSearch.setRouteSearchListener(this);
    }

    /**
     * 开始驾车路径规划
     * @param fromAndTo
     * @param drivingMode
     */
    public void startDriveRoute(RouteSearch.FromAndTo fromAndTo, int drivingMode){
        // fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式
    // 第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, drivingMode, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    /**
     * 开始公交路径规划
     * @param fromAndTo
     * @param busMode
     * @param city
     * @param nightflag
     */
    public void startBusRoute(RouteSearch.FromAndTo fromAndTo, int busMode, String city, int nightflag){
        // fromAndTo包含路径规划的起点和终点，RouteSearch.BusLeaseWalk表示公交查询模式
        // 第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算,1表示计算
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BusLeaseWalk, city,nightflag);
        routeSearch.calculateBusRouteAsyn(query);
    }

    /**
     * 开始骑行路径导航规划
     * @param fromAndTo
     * @param rideMode
     */
    public void startRideRoute(RouteSearch.FromAndTo fromAndTo,int rideMode){
        RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, rideMode);
        routeSearch.calculateRideRouteAsyn(query);
    }

    /**
     * 开始步行导航路径规划
     * @param fromAndTo
     * @param walkMode
     */
    public void startWalkRoute(RouteSearch.FromAndTo fromAndTo,int walkMode){
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo,walkMode);
        routeSearch.calculateWalkRouteAsyn(query);
    }
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        onRouteSearchFinished.routeBusFinished(busRouteResult,i);
    }
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        onRouteSearchFinished.routeDriveFinished(driveRouteResult, i);
    }
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        onRouteSearchFinished.routeWalkFinished(walkRouteResult, i);
    }
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        onRouteSearchFinished.routeRideFinished(rideRouteResult, i);
    }
}
