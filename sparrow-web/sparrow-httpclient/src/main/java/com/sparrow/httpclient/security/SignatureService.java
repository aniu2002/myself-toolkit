package com.sparrow.httpclient.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class SignatureService {

    private static String INPUT_CHARSET = "UTF-8";
    private static String SIGNTYPE = "RSA";

    private static final Logger logger = LoggerFactory.getLogger(SignatureService.class);

    /**
     * 验签
     * @param params
     * @return
     */
    public static boolean verifySign(String sign, String signType, Map<String, Object> params,String public_key) {
        if (!SIGNTYPE.equals(signType))// 暂时只支持RSA签名
            return true;
        Map<String, Object> signParams = SignatureUtil.paraFilter(params);
        String signStr = SignatureUtil.createLinkString(signParams);
        return SignatureUtil.verify(signStr, sign, signType, public_key, INPUT_CHARSET);
    }


    /**
     * 签名
     *
     * @param
     * @return
     */
    public static Map<String, Object> sign(String signType, Map<String, Object> params,String private_key) {
        params.put("signType", signType);
        params.put("sign", "none");
        if (!SIGNTYPE.equals(signType))// 暂时只支持RSA签名
            return params;
        Map<String, Object> signParams = SignatureUtil.paraFilter(params);
        String signStr = SignatureUtil.createLinkString(signParams);
       // logger.info("被签名的串：" + signStr);
        String sign = SignatureUtil.sign(signStr, SIGNTYPE, private_key, INPUT_CHARSET);
        params.put("sign", sign);
        return params;
    }


    public static void main(String[] args) {
        String  pri="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKlXPb0lgiRtRNlk\n" +
                "FXauYrAxVhrHW+yOGF3i4JPBs4NZwqIcb0sNyRen7O/f2o+FgSnLyFGwPVcEtBT4\n" +
                "qhGji9iuIZSaWPIMnVen2NR6KnnGDyOrgWRrCPKhqckZi1/EP/GT1gPe2NfhuS9d\n" +
                "XQcsMKxeT4IiWS+MnhzRm4dFZYy1AgMBAAECgYAPHk3RxDaBQAwkiG3uIZWH+2+f\n" +
                "hmrrjKgaQbYhGusG8A2TYYyCG+H3LbPWmpqts5Kbg6kHuf9P9IiLUsQEPSCv+1fg\n" +
                "FBf3KrP/57Pj0848/T8Ilq1MxXOXTfZoAzx0obCGqlA5vaZyng9JdABGnF/0iHP6\n" +
                "wELaXsEsuhhiNJ6mpQJBAOGR15TITRBYjEMmIAWOLccgulVNWhG7khY1ch+cbSOV\n" +
                "g7sn/3fym5MEn7L8wuT755OJ3DO2iSuYBeAbRHVmZ5sCQQDAL4GxmccKN/3w+tSr\n" +
                "0v9RquhSOgPXKhQ3Bf7JFZZu6VSmzk4AEjIibmcBlomka2Byt9tjV6xNh58t1PQ7\n" +
                "SSnvAkEA2C/p+Rub2G21SGoSyKBCuZVhYH35NAOceJKSpT9Lzo3uqgIks3YequIE\n" +
                "mQvtUn8nrFy1Yg6xGsnh367ihs41vwJBAJBSXKPBZl4/I/0WcCTEY6WxoZs2I/It\n" +
                "kFatsuLQylJp+H73SE33XBzNKZ9gq+aEGD6t4RueGC90nE3vsKTg0ZMCQCDTmm6j\n" +
                "QnucctMAUM8DIKWsyzkUWlQup9jc6wFfF25esdmUrp1YekxOxAhQ5uqYJv6QvVy7\n" +
                "TtE3x/VIE2vYfA0=";
        String pub="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpVz29JYIkbUTZZBV2rmKwMVYa\n" +
                "x1vsjhhd4uCTwbODWcKiHG9LDckXp+zv39qPhYEpy8hRsD1XBLQU+KoRo4vYriGU\n" +
                "mljyDJ1Xp9jUeip5xg8jq4FkawjyoanJGYtfxD/xk9YD3tjX4bkvXV0HLDCsXk+C\n" +
                "IlkvjJ4c0ZuHRWWMtQIDAQAB";
        // 签名
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("service", "dilipay.payment.info.get");
         params.put("partnerId", "80122");
         params.put("bizType", "1");
       String sinStr=(String)sign("RSA", params,pri).get("sign");
        logger.info(sinStr);
        // 验签
        params.clear();
        params.put("service", "dilipay.payment.info.get");
        params.put("partnerId", "80122");
        params.put("bizType", "1");
        logger.info(verifySign(sinStr, "RSA", params, pub)+"");
    }
}
