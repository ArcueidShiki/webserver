package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServerApplication {
    private ServerSocket serverSocket;
    public  WebServerApplication(){
        try {
            System.out.println("正在启动服务端...");
            serverSocket = new ServerSocket(8088);
            System.out.println("服务端启动完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        try {
            System.out.println("等待客户端连接...");
            Socket socket = serverSocket.accept();
            System.out.println("一个客户端连接了...");
            //启动一个线程处理客户端交互
            ClientHandler handler = new ClientHandler(socket);
            new Thread(handler).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        WebServerApplication application = new WebServerApplication();
        application.start();
    }

}
