package com.loveoyh.common.context;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Data
@Slf4j
public class CommonRequestModel implements Serializable {
    private static final long serialVersionUID = 27430784085792743L;
    private String organSign;
    private String employeeId;
    private String employeeName;
    private String loginName;
    private Byte identity;
    private Byte organSignType;
    private Byte bizModel;
    private String headquartersOrganSign;
    private String client;
    private String version;
    private String mac;
    private String ip;

    public CommonRequestModel() {
    }

    public String decodeContent(String content) {
        String decodeString = null;

        try {
            decodeString = URLDecoder.decode(content, "utf-8");
        } catch (UnsupportedEncodingException var4) {
            log.error("decodeContent error", var4);
        }

        return decodeString;
    }

    public String encodeContent(String content) {
        String encodeString = null;

        try {
            encodeString = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException var4) {
            log.error("encodeContent error", var4);
        }

        return encodeString;
    }
}