package com.webserver.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MapTest {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("1","111");
        map.put("2","222");
        map.put("3","333");
        map.put("4","444");
        System.out.println("map.get(\"5\") = " + map.get("5"));
//        Vector 线程安全 synchronized
    }
}
