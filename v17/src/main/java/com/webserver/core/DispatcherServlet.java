package com.webserver.core;

import com.webserver.controller.UserController;
import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

public class DispatcherServlet {
//    private static File root;
//    private static File staticDir;
//    static {
//        try {
//            // root 代表编译后生成的target目录中的class目录
//            // getResource 返回URL， toURI 返回URI   File的一个构造器 接收URI
//            root = new File(DispatcherServlet.class.getClassLoader().getResource(".").toURI());
//            staticDir = new File(root,"static");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
            /*
            在static目录下定位index.html页面
             */
//    }
    public void service(HttpServletRequest request,HttpServletResponse response){
        //2 处理请求
        String path = request.getRequestURI();
        if("/myweb/reg".equals(path)){
            System.out.println("开始处理注册！！！！！！！！！！！！！！！！！！！");
            UserController controller = new UserController();
            controller.reg(request,response);
        }else if("/myweb/login_0".equals(path)){
            System.out.println("开始处理登录！！！！！！！！！！！！！！！！！！！");
            UserController controller = new UserController();
            controller.login(request,response);
        }else{
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
        }
        response.addHeaders("Server","WebServer");
    }
}
