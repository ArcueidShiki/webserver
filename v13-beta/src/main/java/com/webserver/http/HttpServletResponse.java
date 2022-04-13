package com.webserver.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpServletResponse {
    //需要 socket来获取写出流
    private Socket socket;
    private int statusCode = 200;
    private String statusReason = "OK";
    private File contentFile;
    private Map<String,String> headers = new HashMap<>();

    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }

    public void response() throws IOException {

        //1.发送状态行
        sendStatusLine();
        //2.发送响应头
        sendHeaders();
        //3.发送响应正文
        sendContent(contentFile);
    }

    /**
     * 发送一行的方法
     */
    public void println(String line) throws IOException {
        OutputStream os = socket.getOutputStream();
        // getBytes要指定ISO8859-1字符集
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        os.write(data);
        os.write(13);
        os.write(10);
    }
    private void sendContent(File contentFile) throws IOException {
        OutputStream os = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(contentFile);
        byte[] data = new byte[1024*10];
        int len;
        while((len = fis.read(data)) != -1){
            os.write(data,0,len);
        }
    }

    private void sendHeaders() throws IOException {
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for(Map.Entry<String,String> kv : entrySet){
            String key = kv.getKey();
            String value = kv.getValue();
            println(key+": "+value);
        }
        println("");

    }

    private void sendStatusLine() throws IOException {
        println("HTTP/1.1 "+statusCode+" "+statusReason);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;

        // 获取content-Type : text/css application/javascript image/png等等
        String contentType = null;
        try {
            contentType = Files.probeContentType(contentFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("异常检测："+contentType);
        if(contentType != null){
            addHeaders("Content-Type",contentType);
        }
        addHeaders("Content-Length",contentFile.length()+"");
    }
    public void addHeaders(String name,String value){
        headers.put(name,value);
    }
}
