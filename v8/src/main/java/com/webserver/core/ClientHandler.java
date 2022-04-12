package com.webserver.core;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该线程任务负责与指定客户端完成HTTP交互
 * 与客户端交流的流程分成三步:
 * 1:解析请求
 * 2:处理请求
 * 3:发送响应
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    public  ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1 解析请求
            HttpServletRequest request = new HttpServletRequest(socket);
            HttpServletResponse response = new HttpServletResponse(socket);
            //2 处理请求
            String path = request.getUri();
            // root 代表编译后生成的target目录中的class目录
            File root = new File(
                    // getResource 返回URL， toURI 返回URI   File的一个构造器 接收URI
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            File staticDir = new File(root,"static/");
            /*
            在static目录下定位index.html页面
             */
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
            //3.3发送响应正文
            response.response();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally{
            try{
                //发送完响应后，一问一答后断开连接。
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

}
