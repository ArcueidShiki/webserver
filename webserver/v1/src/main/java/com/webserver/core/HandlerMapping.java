package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于进行维护所有业务请求与对应业务类处理，
 * 供DispatcherServlet处理请求时使用
 */
public class HandlerMapping {
    /**
     * 保存请求路径与对应的业务处理
     * key：  请求路径/myweb/reg
     * value: 对应业务处理方法
     */
    private static Map<String,MethodMapping> requestMapping = new HashMap<>();

    static {
        try {
            initRequestMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initRequestMapping() throws Exception{
        //定位controller目录
        File dir = new File(
                HandlerMapping.class.getClassLoader().getResource(
                        "./com/webserver/controller"
                ).toURI()
        );
        //获取.class字节码文件
        File[] subs = dir.listFiles(f->f.getName().endsWith(".class"));
        for (File file : subs) {
            //获取文件名字
            String className = file.getName().substring(0,file.getName().indexOf("."));
            //获取反射对象
            Class cla = Class.forName("com.webserver.controller."+className);
            //判断这个类是否被@Controller进行标注
            if (cla.isAnnotationPresent(Controller.class)) {
                //实例化
                Object controller = cla.newInstance();
                //扫描被@RequestMapping注解所标注的方法
                Method[] methods = cla.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping rm = method.getAnnotation(RequestMapping.class);
                        String path = rm.value();
                        MethodMapping mm = new MethodMapping(controller,method);
                        requestMapping.put(path,mm);
                    }
                }
            }
        }


    }

    /**
     * 根据请求路径获取某个Controller下使用注解@RequestMapping标注处理请求的方法
     * @param path
     * @return
     */
    public static MethodMapping getRequestMapping(String path){
        return requestMapping.get(path);
    }

    /**
     * 内部类
     */
    public static class MethodMapping{
        //Method是处理请求的业务方法，Object为该方法的所属对象
        //为了方便以后反射机制的调用 method.invoke(controller,...);
        private Object controller;
        private Method method;

        public MethodMapping(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }



}
