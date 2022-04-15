package com.webserver.test;

import java.io.*;
import java.util.Arrays;

/**
 * ByteArrayOutputStream test
 */
public class BAOSDemo {
    public static void main(String[] args) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos,"UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pw = new PrintWriter(bw,true);
        byte[] data = "爱尔奎特".getBytes("UTF-8");
        System.out.println(data.length);
        System.out.println(Arrays.toString(data));
        System.out.println("baos.size() 缓冲区大小= " + baos.size());
        System.out.println("====================================");
        baos.write(data);
        data = baos.toByteArray();
        System.out.println(data.length);
        System.out.println(Arrays.toString(data));
        System.out.println("baos.size() 缓冲区大小= " + baos.size());
        System.out.println("====================================");
        pw.println("远野志贵");
        data = baos.toByteArray();
        System.out.println(data.length); // 12+12 +CRLF
        System.out.println(Arrays.toString(data));
        System.out.println("baos.size() 缓冲区大小= " + baos.size());


        pw.close();
    }
}
