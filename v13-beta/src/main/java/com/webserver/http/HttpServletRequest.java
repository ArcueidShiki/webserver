package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    // 需要流来 读取求情
    private Socket socket;
    private String method;
    private String uri;
    private String protocol;
    private Map<String,String> headers = new HashMap<>();
    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        parseRequestLine();
        parseHeaders();
        parseContent();
    }

    private void parseContent() {
        //不解析

    }

    private void parseHeaders() throws IOException {
        while(true){
            String line = readLine();
            if(line.isEmpty()) break;
            System.out.println("消息头:"+line);
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1]);
        }
    }

    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        if(line.isEmpty()) throw new EmptyRequestException();
        System.out.println("请求行: "+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];
    }

    public String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        StringBuilder builder = new StringBuilder();
        int d;
        char pre = 'a';
        char cur = 'a';
        // 读到CR LF结束
        while((d = in.read())!=-1){
            cur = (char)d;//本次读取到的字符
            if(pre==13&&cur==10){//判断是否连续读取到了回车和换行符
                break;
            }
            builder.append(cur);
            pre = cur;//在进行下次读取字符前将本次读取的字符记作上次读取的字符
        }
        return builder.toString().trim();
    }
    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    //1.解析请求行
    //2.解析消息头
    //3.解析消息正文
}
