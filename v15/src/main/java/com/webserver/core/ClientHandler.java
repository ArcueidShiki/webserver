package com.webserver.core;
import com.webserver.http.EmptyRequestException;
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
            DispatcherServlet servlet = new DispatcherServlet();
            servlet.service(request,response);
            //3.3发送响应正文
            response.response();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EmptyRequestException e){

        }finally{
            try{
                //发送完响应后，一问一答后断开连接。
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

}
