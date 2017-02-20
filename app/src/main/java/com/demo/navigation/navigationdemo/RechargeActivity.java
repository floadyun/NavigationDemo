package com.demo.navigation.navigationdemo;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.demo.navigation.navigationdemo.alipay.AlipayHelper;
import com.demo.navigation.navigationdemo.wxpay.Constants;
import com.demo.navigation.navigationdemo.wxpay.WXPayHelper;
import com.tencent.mm.sdk.modelpay.PayReq;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yixiaofei on 2017/2/20 0020.
 */

public class RechargeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private static final int ALI_PAY_TYPE = 1;

    private static final int WECHAT_PAY_TYPE = 2;

    @BindView(R.id.recharge_radio_group)
    RadioGroup rechargeRadio;

    private int payType = ALI_PAY_TYPE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        rechargeRadio.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i){
            case R.id.alipay_radio_btn:
                payType = ALI_PAY_TYPE;
                break;
            case R.id.wechat_radio_btn:
                payType = WECHAT_PAY_TYPE;
                break;
        }
    }

    /**
     * 支付调用
     * @param view
     */
    public void toRecharge(View view) {
        if(payType==ALI_PAY_TYPE){
            AlipayHelper.sharedInstance(this).startPay(this);
        }else if(payType==WECHAT_PAY_TYPE){
            PayReq req = new PayReq();
            req.appId = Constants.APP_ID;
            req.partnerId = "";
            req.prepayId = "";
            req.packageValue = "";
            req.nonceStr = "";
            req.timeStamp = "";
            req.sign = "";
            WXPayHelper.sharedInstance(this).startPay(req);
        }
    }
}
