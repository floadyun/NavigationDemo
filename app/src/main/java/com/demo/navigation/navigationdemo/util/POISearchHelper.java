package com.demo.navigation.navigationdemo.util;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

/**
 * Created by yixiaofei on 2017/2/17 0017.
 */

public class POISearchHelper implements PoiSearch.OnPoiSearchListener{

    public interface OnPoiSearchFinished{
        void searchSuccessed(PoiResult poiResult);
        void searchFailed(PoiResult poiResult);
    }

    private static POISearchHelper poiSearchHelper;

    private PoiSearch.Query poiQuery;

    private PoiSearch poiSearch;

    private Context context;

    private OnPoiSearchFinished onPoiSearchFinished;

    public POISearchHelper(Context context){

        this.context = context.getApplicationContext();
    }

    public static synchronized POISearchHelper sharedInstance(Context context){
        if(poiSearchHelper==null){
            poiSearchHelper = new POISearchHelper(context);
        }
        return poiSearchHelper;
    }

    /**
     * 设置搜索完成监听
     * @param onPoiSearchFinished
     */
    public void setOnPoiSearchFinished(OnPoiSearchFinished onPoiSearchFinished){
        this.onPoiSearchFinished = onPoiSearchFinished;
    }
    /**
     * 根据关键字开始搜索周边POI
     */
    public void startSearch(String keyWord,String searchType,String cityCode,int poiNum){
        poiQuery = new PoiSearch.Query(keyWord, searchType, cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        poiQuery.setPageSize(poiNum);// 设置每页最多返回多少条poiitem
        //poiQuery.setPageNum(currentPage);//设置查询页码
        poiSearch = new PoiSearch(context.getApplicationContext(), poiQuery);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    /**
     * 根据经纬度搜索周边POI
     * @param latitude
     * @param longitude
     * @param radius
     */
    public void searchAroud(double latitude,double longitude,int radius){
        PoiSearch.Query poiQuery = new PoiSearch.Query("", "餐饮服务");
        LatLonPoint centerPoint = new LatLonPoint(latitude, longitude);
        PoiSearch.SearchBound searchBound = new PoiSearch.SearchBound(centerPoint, radius);

        poiSearch = new PoiSearch(context, poiQuery);
        poiSearch.setBound(searchBound);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        //搜索成功
        if(resultCode==1000){
            if(onPoiSearchFinished!=null){
                onPoiSearchFinished.searchSuccessed(poiResult);
            }
        }else{
            if(onPoiSearchFinished!=null){
                onPoiSearchFinished.searchFailed(poiResult);
            }
        }
    }
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
