package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        // 1 解析问请求
        try {
            HttpServletRequest request = new HttpServletRequest(socket);
            HttpServletResponse response = new HttpServletResponse(socket);
            // 2 处理请求
            DispatcherServlet servlet = new DispatcherServlet();
            servlet.service(request,response);
            // 3 发送响应
            response.response();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EmptyRequestException e){

        }finally {
            try{
                //发送完所有响应后 断开连接
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
