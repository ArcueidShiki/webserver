package com.webserver.core;

import com.webserver.controller.ArticleController;
import com.webserver.controller.UserController;
import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

public class DispatcherServlet {

    public void service(HttpServletRequest request,HttpServletResponse response){
        //2 处理请求 拦截请求
        String path = request.getRequestURI();
        if("/myweb/reg".equals(path)){
            System.out.println("开始处理注册！！！！！！！！！！！！！！！！！！！");
            UserController controller = new UserController();
            controller.reg(request,response);
        }else if("/myweb/login_0".equals(path)){
            System.out.println("开始处理登录！！！！！！！！！！！！！！！！！！！");
            UserController controller = new UserController();
            controller.login(request,response);
        }else if("/myweb/writeArticle".equals(path)){
            System.out.println("开始处理发布文章！！！！！！！！！");
            ArticleController controller = new ArticleController();
            controller.writeArticle(request,response);
        }else if("/myweb/showAllUser".equals(path)){
            System.out.println("开始处理动态页面显示所有用户列表！！！！！！！！！！！！");
            UserController controller = new UserController();
            controller.showAll(response);
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
