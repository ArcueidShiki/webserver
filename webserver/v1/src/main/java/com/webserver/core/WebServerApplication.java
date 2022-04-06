package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  webServer是模拟Tomcat得一个web容器。主要两个功能
 *  1.管理部署在容器中的服务段应用
 *  2.与客户端(浏览器)建立TCP链接(3握手4挥手),基于http协议交互
 *  使得浏览器可以调用部署在web容器中得服务端应用，实现客户端访问服务端提供的对应功能(浏览，查询，请求等业务)
 *
 *  服务端应用也称为网络应用，每个网络应用通常都包含网页，素材，逻辑代码等 俗称一个网站
 *
 *  2个属性 serverSocket;
 *  3个方法  构造(new serverSocket,监听端口)
 *          start(死循环(accept阻塞,等待建立连接,创建处理请求的线程人物类,execute))
 *          main(启动)
 */
public class WebServerApplication {
    private ServerSocket serverSocket;
    // 服务端容器中需要利用线程 去处理不同用户的请求。纳入线程池
    // ExecutorService是一个interface,实现execute方法时，传进来的任务要实现Runnable接口
    private ExecutorService threadPool;

    public WebServerApplication(){
        try {
            System.out.println("正在启动服务端...");
            serverSocket = new ServerSocket(8088);
            threadPool = Executors.newFixedThreadPool(50);
            System.out.println("服务端启动完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        //构造后 要是服务端一直处于阻塞状态 不能挂掉
        try {
            while(true){
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                System.out.println("一个客户端连接了！");
                //创建处理客户端请求交互的线程
                ClientHandler clientHandler = new ClientHandler(socket);
                threadPool.execute(clientHandler);
                //纳入线程池管理执行/排队
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        WebServerApplication web = new WebServerApplication();
        web.start();
    }

}
