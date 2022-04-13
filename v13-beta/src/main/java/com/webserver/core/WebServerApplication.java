package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServerApplication {
    private ServerSocket serverSocket;
    public WebServerApplication(){
        try {
            System.out.println("服务端启动...");
            serverSocket = new ServerSocket(8088);
            System.out.println("服务端启动完毕...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
            try {
                while (true) {
                    System.out.println("等待客户端连接...");
                    Socket socket = serverSocket.accept();
                    System.out.println("一个客户端连接了！");
                    ClientHandler handler = new ClientHandler(socket);
                    Thread t = new Thread(handler);
                    t.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static void main(String[] args) {
        WebServerApplication app = new WebServerApplication();
        app.start();
    }
}
