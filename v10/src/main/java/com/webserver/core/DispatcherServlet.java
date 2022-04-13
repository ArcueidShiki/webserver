package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

public class DispatcherServlet {
    public void service(HttpServletRequest request,HttpServletResponse response){
        //2 处理请求
        String path = request.getUri();

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
}
