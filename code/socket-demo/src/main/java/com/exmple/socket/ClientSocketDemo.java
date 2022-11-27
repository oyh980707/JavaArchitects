package com.exmple.socket;

import java.io.*;
import java.net.Socket;

public class ClientSocketDemo {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 9090);
//            client.setSendBufferSize(20);
//            client.setTcpNoDelay(false);
//            client.setOOBInline(false);
//
//            OutputStream os = client.getOutputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//            while (true) {
//                String s = br.readLine();
//                byte[] bytes = s.getBytes();
//                for(int i=0 ; i<bytes.length ; i++) {
//                    os.write(bytes[i]);
//                }
//            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

}
