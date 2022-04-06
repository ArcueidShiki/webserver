package com.webserver.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 记录所有关于HTTP协议相关的内容
 */
public class HttpContext {
    /**
     * 所有资源后缀与头部信息 Content-Type的对应关系
     * key : suffix
     * value:Content-Type 头对应的值
     */
    private static Map<String,String> mimeMapping = new HashMap<>();

    static{ initMimeMapping();}

    /**
     * 静态属性初始化 ,加载properties属性 映射map
     */
    private static void initMimeMapping() {
        try{
            //创建一个properties对象 extends HashTable
            Properties properties = new Properties();
            /*
            加载对应的properties 文件，获取HttpContext.class获取反射对象找路径
             */
            properties.load(HttpContext.class.getResourceAsStream("web.properties"));
            //properties对象 extends HashTable ,k,v为object 要toString
            properties.forEach((k,v) ->mimeMapping.put(k.toString(),v.toString()));
            //也可以通过entrySet方式获取kv对
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 根据资源后缀名 返回Content-Type的值
     *
     */
    public static String getMimeType(String suffix){
        return mimeMapping.get(suffix);
    }
    // for test
//    public static void main(String[] args){
//        String type = getMimeType("png");
//        System.out.println(type);
//    }

}
