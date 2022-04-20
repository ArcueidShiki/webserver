package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 维护所有请求与对应业务处理类的关系
 * 存一个 static map(String path,Object javabean)
 * 每次就不用扫描了(因为 method并不会动态变化)。
 */
public class HandlerMapping {
    private static Map<String,MethodMapping> mapping = new HashMap<>();
    //不能提供public 和 getter 方法 防止clear()
    public static Object getController(String path){
        return mapping.get(path).getController();
    }
    public static Method getMethod(String path){
        return mapping.get(path).getMethod();
    }
    public static boolean contains(String path){
        return mapping.containsKey(path);
    }

    static{
        initMapping();
        System.err.println(mapping);
    }
    /**
     * 初始化map的方法
     */
    private static void initMapping(){
        File dir = null;
        try {
            // 定位 controller 包。跨包 要用classLoader
            dir = new File(HandlerMapping.class.getClassLoader().getResource(
                    "./com/webserver/controller").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        /**
         * 扫描工作只做一次 、/myweb/reg mapping (POJO- Javabean)controller.reg;
         * 存一个 static map(String path,Object javabean) 每次就不用扫描了(method并不会动态变化)。
         * 通过key取value O(1)
         */
        System.err.println(dir.getName()); //controller 不能用 endsWith(".class") 过滤
        File[] files = dir.listFiles(f->f.getName().endsWith(".class"));
        System.err.println(files.length);
        for(File file:files){
            System.err.println(file.getName());
            try {
                //包名只能用. 不是斜杠
                Class cla = Class.forName("com.webserver.controller."+file.getName().substring(0,file.getName().indexOf(".class")));
                System.err.println(cla.getName());
                if(cla.isAnnotationPresent(Controller.class)){
                    System.err.println("进入controller");
                    Object controller = cla.newInstance();
                    Method[] methods = cla.getDeclaredMethods();
                    for(Method method : methods){
                        if(method.isAnnotationPresent(RequestMapping.class)){
                            MethodMapping methodMapping = new MethodMapping(controller,method);
                            mapping.put("/myweb/"+method.getName(),methodMapping);
//                            mapping.put(method.getAnnotation(RequestMapping.class).value(),methodMapping);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获取反射对象
        }
    }
    /**
     * 每个实例记录一个业务方法以及所属的controller对象
     * inner class javabean
     */
    public static class MethodMapping{
        private Object controller;
        private Method method;

        public MethodMapping() {
        }

        public MethodMapping(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getController() {
            return controller;
        }


        public Method getMethod() {
            return method;
        }

    }
}
