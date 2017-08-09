package com.sparrow.httpclient.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


public class SignatureUtil {

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, Object> paraFilter(Map<String, Object> sArray) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            if (isBlank(key) || isBlank(sArray.get(key))) {
                continue;
            } else if (key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("signType")) {
                continue;
            }
            result.put(key, sArray.get(key));
        }
        return result;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, Object> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (isBlank(key) || isBlank(params.get(key))) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(params.get(key));
        }
        return sb.toString();
    }


    /**
     * 请求数据RSA签名
     *
     * @param signStr       待签名数据
     * @param signType
     * @param privateKey    接入方私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String signStr, String signType, String privateKey, String input_charset) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(signType);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
            signature.initSign(priKey);
            signature.update(signStr.getBytes(input_charset));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * RSA验签名检查
     *
     * @param signStr       待签名数据
     * @param sign          签名值
     * @param signType
     * @param public_key    公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String signStr, String sign, String signType, String public_key, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(signType);
            byte[] encodedKey = Base64.decode(public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(signStr.getBytes(input_charset));
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private static boolean isBlank(Object param) {
        if (null == param || "".equals(param))
            return true;
        else
            return false;
    }
}
