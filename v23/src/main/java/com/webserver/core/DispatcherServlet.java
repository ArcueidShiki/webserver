package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.controller.ArticleController;
import com.webserver.controller.ToolsController;
import com.webserver.controller.UserController;
import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

public class DispatcherServlet {

    public void service(HttpServletRequest request,HttpServletResponse response){
        //2 处理请求 拦截请求 POST请求 有正文 GET没有正文
        String path = request.getRequestURI();
        System.err.println(path);
        File dir = null;
        try {
            // 定位 controller 包。跨包 要用classLoader
            dir = new File(DispatcherServlet.class.getClassLoader().getResource(
                    "./com/webserver/controller").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.err.println(dir.getName()); //controller 不能用 endsWith(".class") 过滤
        File[] files = dir.listFiles(f->f.getName().endsWith(".class"));
        System.err.println(files.length);
        for(File file:files){
            System.err.println(file.getName());
            try {
                Class cla = Class.forName("com.webserver.controller."+file.getName().substring(0,file.getName().indexOf(".class")));
                System.err.println(cla.getName());
                if(cla.isAnnotationPresent(Controller.class)){
                    System.err.println("进入controller");
                    Object obj = cla.newInstance();
                    Method[] methods = cla.getDeclaredMethods();
                    for(Method method : methods){
                        if(method.isAnnotationPresent(RequestMapping.class) &&
                                method.getAnnotation(RequestMapping.class).value().equals(path)){
                            method.invoke(obj,request,response);
                            System.err.println("method has invoked");
                            return;
                         }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获取反射对象
        }
        File file = new File("webapps",path);
        System.out.println("资源是否存在 : "+file.exists());
        if(file.isFile()) {
            response.setContentFile(file);

        }else{
            //页面不存在
            response.setStatusCode(404);
            response.setStatusReason("Not Found");
            file = new File("webapps","root/404.html");
            response.setContentFile(file);
        }
        response.addHeaders("Server","WebServer");
    }
}
