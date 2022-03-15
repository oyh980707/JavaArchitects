package com.loveoyh.common.utils;

import com.loveoyh.common.context.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 *URL签名算法
 */
public class SignOauthUtil {

    private static Logger logger = LoggerFactory.getLogger(SignOauthUtil.class);

    private static final String SIGN_METHOD_MD5 = "md5";

    public static boolean checkSign(String paramSign, ActionContext context, String secretKey){
        try {
            String sign = signRequest(getSourceSignString(context),secretKey);
            context.setServerSign(sign);
            return sign.equals(paramSign);
        } catch (IOException e) {
            logger.error("checkSign io error",e );
        } catch (NoSuchAlgorithmException e) {
            logger.error("checkSign NoSuchAlgorithmException error",e );
        }
        return false;
    }

    /**
     * 按字典排序生成源字符串
     * @param params
     * @return
     */
    public static String getSourceSignString(ActionContext params){
        String data = StringUtils.trim(params.getData());
        return data;
    }
    /**
     * 根据参数生成签名
     *
     * @param params     参数
     * @return 签名
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String signRequest(String params, String secretKey) throws IOException,
            NoSuchAlgorithmException {

        if (Objects.isNull(params) || StringUtils.isBlank(secretKey)) {
            return null;
        }

        StringBuffer buffer = new StringBuffer(params);

        buffer.append("=");

        buffer.append(secretKey);

        logger.debug("签名前的参数串：" + buffer.toString());

        byte[] bytes = encryptMD5(buffer.toString());

        String result = byte2hex(bytes);

        logger.debug("签名后的参数串：" + result);

        return result;
    }

    /**
     * MD5加密
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static byte[] encryptMD5(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] btInput = data.getBytes("utf-8");
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst = MessageDigest.getInstance(SIGN_METHOD_MD5);
        // 使用指定的字节更新摘要
        mdInst.update(btInput);
        // 获得密文
        byte[] md = mdInst.digest();
        return md;
    }

    /**
     * 二进制转化为大写的十六进制
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static void main(String[] args){
        ActionContext c = new ActionContext();
//        JSONObject data = new JSONObject();
//        data.put("name", "");
//        data.put("phone", "");
//        data.put("roleId", "");
//        data.put("pageSize", 50);
//        data.put("pageNo", 1);
        c.setClient("C#");
//        c.setData(JSONUtils.obj2JSON(data));
        c.setData("{\"phone\":\"\",\"roleId\":\"\",\"pageNo\":1,\"name\":\"\",\"pageSize\":50}");
        c.setIp("127.0.0.1");
//        AppActionContext appContext = new AppActionContext();
//        appContext.setAppMac("123.234");
//        c.setAppContext(appContext);
        c.setVersion("v12.0");
//        String sign = signRequest(getSourceSignString(c),"JCCLJOoasBdsfK");
        boolean result = checkSign("",c,"JKKLJOoasdlfC");
        System.out.print(result);
    }
}
