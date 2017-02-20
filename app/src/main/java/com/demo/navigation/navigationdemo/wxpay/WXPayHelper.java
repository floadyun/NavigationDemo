package com.demo.navigation.navigationdemo.wxpay;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by yixiaofei on 2017/2/20 0020.
 */

public class WXPayHelper implements IWXAPIEventHandler{

    private static WXPayHelper wxPayHelper;
    //支付请求
    private PayReq payReq;

    private IWXAPI iwxapi;

    private Context context;

    public WXPayHelper(Context context){

        payReq = new PayReq();

        iwxapi = WXAPIFactory.createWXAPI(context,null);

        iwxapi.registerApp(Constants.APP_ID);

        context = context.getApplicationContext();
    }
    public synchronized static WXPayHelper sharedInstance(Context context){
        if(wxPayHelper==null){
            wxPayHelper = new WXPayHelper(context);
        }
        return wxPayHelper;
    }
    /**
     * 开始支付
     */
    public void startPay(PayReq req){
        iwxapi.sendReq(req);
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.errCode==1000){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setMessage("支付信息:"+baseResp.errCode);
            alertBuilder.create().show();
        }
    }
}