package com.loveoyh.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.loveoyh.common.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 把请求的公共参数去掉只保留data业务参数，校验参数
 * @author hanweiwang
 * @date 2019-08-12
 * @mondify
 * @copyright
 */
public class ParameterCheckServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ParameterCheckServletRequestWrapper.class);
    private byte[] requestBody;//暂存所有参数
    private byte[] newRequestBody; //只暂存业务参数data,用于controller的@requestbody获取参数
    private String allParamsJSON;
    private Charset charSet;
    private Map<String,String> headersMap;

    public ParameterCheckServletRequestWrapper(HttpServletRequest request) {
        super(request);

        //缓存请求body
        try {
            String requestBodyStr = getRequestPostStr(request);
            if (StringUtils.isNotBlank(requestBodyStr)) {
//                String formatString = requestBodyStr.replace("\"", "'");
                JSONObject resultJson = JSONUtils.parse2Object(requestBodyStr, JSONObject.class);

                String[] obj = resultJson.keySet().toArray(new String[0]);
                String data = "";
                JSONObject dataJSON = null;
                for (String o : obj) {
                    if("data".equals(o)){
                        data = resultJson.get(o).toString();
//                        dataJSON = JSONUtils.json2Obj(data, JSONObject.class);
                    }
                    resultJson.put(o, StringUtils.trimToEmpty(resultJson.get(o)!=null?resultJson.get(o).toString():""));
                }
                allParamsJSON = resultJson.toJSONString();
                requestBody = allParamsJSON.getBytes(charSet);
                newRequestBody = data.getBytes(charSet);
            } else {
                requestBody = new byte[0];
                allParamsJSON = "";
            }
        } catch (IOException e) {
            logger.error("cache request by to byte[] error", e);
        }
        // 提取原本的HEADER内容
        Enumeration enumeration = request.getHeaderNames();
        headersMap = new HashMap<String,String>();
        while(enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            headersMap.put(name, request.getHeader(name));
        }

    }
    //返回所有的请求参数
    public String getAllParams(){
        return allParamsJSON;
    }

    public String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        String charSetStr = request.getCharacterEncoding();
        if (charSetStr == null) {
            charSetStr = "UTF-8";
        }
        charSet = Charset.forName(charSetStr);

        return StreamUtils.copyToString(request.getInputStream(), charSet);
    }

    /**
     * 重写 getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() {
        if (newRequestBody == null) {
            newRequestBody = new byte[0];
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(newRequestBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 重写 getReader()
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public void addHeader(String name,String value) {
        headersMap.put(name, value);
    }

    public void addHeaders(Map<String,String> headerMap) {
        if(headerMap==null){
            return ;
        }
        headersMap.putAll(headerMap);
    }

    @Override
    public String getHeader(String name) {
        return headersMap.get(name);
    }

    @Override
    public Enumeration getHeaderNames() {
        Set<String> set = new HashSet<String>(headersMap.keySet());
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            String n = e.nextElement();
            set.add(n);
        }
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (headersMap.containsKey(name)) {
            values = Arrays.asList(headersMap.get(name));
        }
        return Collections.enumeration(values);

    }

    public Map<String, String> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(Map<String, String> headersMap) {
        this.headersMap = headersMap;
    }
}
