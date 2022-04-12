package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

public class DispatcherServlet {
    private static File root;
    private static File staticDir;
    static {
        try {
            root = new File(            // root 代表编译后生成的target目录中的class目录
                    // getResource 返回URL， toURI 返回URI   File的一个构造器 接收URI
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        staticDir = new File(root,"static/");
            /*
            在static目录下定位index.html页面
             */
    }
    public void service(HttpServletRequest request,HttpServletResponse response){
        //2 处理请求
        String path = request.getUri();

        File file = new File(staticDir,path);
        System.out.println("资源是否存在 : "+file.exists());

        if(file.isFile()) {
            response.setContentFile(file);
        }else{
            //页面不存在
            response.setStatusCode(404);
            response.setStatusReason("Not Found");
            file = new File(staticDir,"root/404.html");
            response.setContentFile(file);
        }
    }
}
