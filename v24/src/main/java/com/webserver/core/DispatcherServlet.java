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

        if(HandlerMapping.getController(path) != null){
            Object obj = HandlerMapping.getController(path);
            Method method = HandlerMapping.getMethod(path);
            try {
                method.invoke(obj,request,response);
                return;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
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