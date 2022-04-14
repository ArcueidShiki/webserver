package com.webserver.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class TestDecode {
    public static void main(String[] args) {
        String line = "/myweb/login?username=%E5%86%B2%E7%94%B0&password=123";
        try {
            //URLDecoder 只对 %16进制的敏感
            line = URLDecoder.decode(line, "utf-8");
            System.out.println(line);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
