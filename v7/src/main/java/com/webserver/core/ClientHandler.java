package com.webserver.core;
import com.webserver.http.HttpServletRequest;

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
//            System.out.println("资源是否存在 : "+file.exists());
            int statusCode;
            String statusReason;
            if(file.isFile()) {
                statusCode = 200;
                statusReason = "OK";
            }else{
                //页面不存在
                statusCode = 404;
                statusReason = "NotFound";
                file = new File(staticDir,"root/404.html");
            }
            //3.3发送响应正文
            sendContent(file,statusCode,statusReason);
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

    public void sendContent(File file,int statusCode,String statusReason) throws IOException {
        println("HTTP/1.1 "+statusCode+statusReason);
        //3.2响应头
        println("Content-Type: text/html");
        println("Content-Length: " + file.length());
        println("");//空串没有字串，字节数组长度为
        OutputStream out = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[1024 * 10];
        int len;
        while ((len = fis.read(data)) != -1) {
            out.write(data, 0, len);
        }
    }

    public void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13); // CR
        out.write(10); // LF
    }


}
