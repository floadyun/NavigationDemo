package com.demo.navigation.navigationdemo.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import java.util.Map;

/**
 * Created by yixiaofei on 2017/2/20 0020.
 */

public class AlipayHelper {

    private static AlipayHelper alipayHelper;

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2017021605696446";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";
    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "";

    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIC8dHKamxnSinNpNn4Usk0t6tTPN/uXdzbItkD5SwRgPTdi8KB/+x+q2VHTceP3yOzsXhRAFRnaLbd3Z2UAGeWfCURL+rKt2OuDPJk6pGg/xVY36NhbhxvcvQFE0DemjLaoZAUFO10mnml2pRm2z25dFWaj+/rdp3mnna4TDyIbAgMBAAECgYB5bsq9C98/9VyJzvYK6gTiTmzTvn+FP/PA5oUcNssvXoSACUEHdJFx5pvF1pj4u9N4wOFhYq1EzIwRIn2SRQ0nM27W/HfdCEuB1GaqslJp8E882/8T1TKCT20qOUFCwJE42LJhGo7hAc+6oNbdiLaAf0jGyMnf4MMAJe7i1f6bKQJBANc4Ni45Io8DNq9Ct7nh1NwYUSFNKDvA3ZrfaGGQaZpJvP12oHcnbDIP9QxCW+VxjMap7hJRzEeoczPWD2B1W98CQQCZISN4gVlCGjy9ckNLrLBXUWnWMXoqBsFwpEoouhrGeszNmImzfBaNx3Bb6zGr7kstxLUtFizvZB5fJPhUHoFFAkBWVbRWYpEccZuUPt1Y8eDj0dVp2HvVTI8ZO5mx2a6jOAVaYCYK5oOeqYwRuOUIa76fSze3nZym+koMd0h/11RdAkB5eo+gLsU3qobbJ8V5SEMQ0lZrBQ9MPdXB3aOzUYiiQpMs20EKcXuW+EwLr7bVFZLweSfd38TAGSX7BuI2IYkdAkEAwGFTLATvQSvpqLVEEc54N+2VTyPKH6WD/TnQdiP991JJNVC8vk3R6gMkfmzXTKV4iXCCcaVSJ1Kn3hzwM03SSg==";

    private static final int SDK_PAY_FLAG = 1;

    private Context context;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Toast.makeText(context, "支付状态:"+resultStatus, Toast.LENGTH_SHORT).show();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                      //  Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                      //  Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    public AlipayHelper(Context context){
        this.context = context.getApplicationContext();
    }
    public synchronized static AlipayHelper sharedInstance(Context context){
        if(alipayHelper==null){
            alipayHelper = new AlipayHelper(context);
        }
        return alipayHelper;
    }

    /**
     * 开始支付
     */
    public void startPay(final Activity activity){
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.e("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
