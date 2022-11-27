package com.exmple.socket;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketDemo {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(9091), 0);
            serverSocket.setReceiveBufferSize(20);
            serverSocket.setReuseAddress(false);
            serverSocket.setSoTimeout(0);
            System.out.println("wai in ...");
        } catch (Exception e) {
        } finally {
            if(null != serverSocket) {
                try {
                    serverSocket.close();
                }catch (Exception e) {}
            }
        }

        while (true) {
            try {
                System.in.read();

                Socket accept = serverSocket.accept();
                System.out.println("accept port:"+accept.getPort());

                accept.setKeepAlive(false);
                accept.setOOBInline(false);
                accept.setReuseAddress(false);
                accept.setReceiveBufferSize(20);
                accept.setSoTimeout(0);
                accept.setSoLinger(true, 0);
                accept.setTcpNoDelay(false);
                accept.setSendBufferSize(20);

                new Thread(()->{
                    while(true) {
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                            char[] buff = new char[1024];
                            br.read(buff);
                            if(buff.length > 0) {
                                System.out.println("client accept data is : " + new String(buff, 0, buff.length));
                            }else if (buff.length == 0){
                                System.out.println("client accept not data.");
                                continue;
                            }else {
                                System.out.println("client read -1 ...");
                                accept.close();
                                break;
                            }
                        } catch (Exception e) {}
                    }
                }).start();
            } catch (Exception e) {}
        }

    }

}
