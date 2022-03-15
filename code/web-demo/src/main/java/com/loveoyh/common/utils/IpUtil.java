package com.loveoyh.common.utils;

import com.loveoyh.common.entity.IpRange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

public class IpUtil {
    private static final Logger log = LoggerFactory.getLogger(IpUtil.class);

    private static List<IpRange> privateNetIp = new LinkedList();

    public static String getRealIp(HttpServletRequest request)
    {
        String ip = request.getHeader("x-real-ip");
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("x-forwarded-for");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
        }

        ip = getFirstIP(ip);
        return ip;
    }

    public static String getFirstIP(String ipstr) {
        String ip = ipstr;
        String[] arr = ipstr.split(",");
        String[] var6 = arr;
        int var5 = arr.length;

        for (int var4 = 0; var4 < var5; var4++) {
            String item = var6[var4];
            if (item.contains(".")) {
                ip = item.trim();
                break;
            }
        }
        return ip;
    }

    public static long ip2Long(String ip) {
        if (StringUtils.isBlank(ip)) {
            log.error("ip can't be null or empty.");
            return -1L;
        }

        ip = ip.replace(" ", "");
        ip = ip.replace("　", "");
        String[] ips = ip.trim().split("\\.");
        long longIp = 0L;
        try {
            longIp = Long.parseLong(ips[0]) * 256L * 256L * 256L + Long.parseLong(ips[1]) * 256L * 256L + Long.parseLong(ips[2]) * 256L + Long.parseLong(ips[3]);
        }
        catch (Exception e) {
            log.error("ip error: " + ip, e);
        }
        return longIp;
    }

    public static boolean isPrivateNet(String ip)
    {
        long currentIp = ip2Long(ip);
        for (IpRange ipRang : privateNetIp) {
            if (ipRang.contain(currentIp)) {
                return true;
            }
        }
        return false;
    }

    static
    {
        privateNetIp.add(new IpRange(ip2Long("127.0.0.1")));
        privateNetIp.add(new IpRange(ip2Long("0.0.0.0")));
        privateNetIp.add(new IpRange(ip2Long("10.0.0.0"), ip2Long("10.255.255.255")));
        privateNetIp.add(new IpRange(ip2Long("172.16.0.0"), ip2Long("172.31.255.255")));
        privateNetIp.add(new IpRange(ip2Long("192.168.0.0"), ip2Long("192.168.255.255")));
    }
}