package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类的每一个实例表示客户端发送的一个请求
 * 每个请求包含三部分：
 * 1、请求行
 * 2、消息头
 * 3、消息正文
 */
public class HttpServletRequest {
    private Socket socket;
    //请求行相关信息
    private String method;//请求方式
    private String uri;//抽象路径
    private String protocol;//协议版本

    private String requestURI;//记录url中"?"左侧的请求部分
    private String queryString;//记录url中"?"右侧的参数部分
    private Map<String,String> parameters = new HashMap<>();//记录每一组参数

    //消息头相关信息
    private Map<String,String> headers = new HashMap<>();

    //消息正文相关信息
    private byte[] contentData;

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1.解析请求行
        parseRequestLine();
        //2.解析消息头
        parseHeaders();
        //3.解析消息正文
        parseContent();
    }

    /**
     * 解析请求行
     */
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        //判断是否是空请求
        if (line.isEmpty()) {
            throw new EmptyRequestException();
        }
        System.out.println("请求行：" + line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        //进一步解析uri
        parseUri();
        protocol = data[2];
        System.out.println("请求方式：" + method);
        System.out.println("抽象路径：" + uri);
        System.out.println("协议版本：" + protocol);
    }

    /**
     * 进一步解析uri
     */
    private void parseUri() {
        /*
        首先进行判断uri是否含有参数，判断的依据是uri中是否包含"?"
        1、不带参数(不包含?)：/myweb/reg.html
            直接将uri的值赋值给requestURI
        2、带参数(包含?)：/myweb/reg?username=baojq&password=888888&nickname=baozi&age=30
            2.1将uri按照"?"进行拆分出两个部分，第一个部分为请求抽象路径部分，将该部分赋值给requestURI
            2.2将uri按照"?"进行拆分的第二个部分赋值给queryString
                2.2.1对于queryString进行进一步拆分，先按照"&"拆分出每个参数，再将每个参数按照"="
                     进行拆分，将参数名存储到parameters的key中，将参数值存储到parameters的value中
            2.3解析过程中需要注意uri的几种特殊情况
                2.3.1/myweb/reg?   也就是uri中包含?但是没有参数部分
                2.3.2/myweb/reg?username=&password=888888&nickname=baozi&age=30
                                        也就是参数部分可能只有参数名没有参数值
         */
        String[] data = uri.split("\\?");
        requestURI = data[0];
        if (data.length > 1) { //有参数
            queryString = data[1];
            //拆分每一组参数
            parseParameters(queryString);
        }

        System.out.println("requestURI:" + requestURI);
        System.out.println("queryString:" + queryString);
        System.out.println("parameters:" + parameters);
    }

    /**
     * 拆分请求参数，用于初始化parameters这个Map
     * @param line
     */
    private void parseParameters(String line){
        //拆分出每一组参数，存到数组中[username=baojq,password=888888,nickname=baozi,age=3]
        String[] paraArr = line.split("&");
        for (String para : paraArr) {
            //遍历拆分每一个参数名和参数值[username,baojq]
            String[] paras = para.split("=");
            if (paras.length > 1) {
                parameters.put(paras[0],paras[1]);
            } else {
                parameters.put(paras[0],null);
            }

        }
    }

    /**
     * 解析消息头
     */
    private void parseHeaders() throws IOException{

        while (true){
            String line = readLine();
            //如果读取的是空字符串就说明单独读取了CRLF，消息头读取完毕
            if (line.isEmpty()) {
                break;//终止循环
            }
            // Connection: keep-alive
            System.out.println("消息头：" + line);
            String[] data = line.split(":\\s");
            String key = data[0];
            String value = data[1];
            headers.put(key,value);

        }
        System.out.println("headers:" + headers);
    }

    /**
     * 解析消息正文
     */
    private void parseContent() throws IOException {
        //判断一下Content-Length是否存在
        if (headers.containsKey("Content-Length")) {
            //获取正文长度
            int len = Integer.parseInt(headers.get("Content-Length"));
            contentData = new byte[len];
            //读取正文数据
            InputStream in = socket.getInputStream();
            in.read(contentData);//一次性块读写所有正文数据

            //根据消息头Content-Type确认正文类型，以便分支处理
            if (headers.containsKey("Content-Type")) {
                String contentType = headers.get("Content-Type");
                //分支判断不同类型进行不同处理
                if ("application/x-www-form-urlencoded".equals(contentType)) {
                    //原GTE请求提交表单时，抽象路径中？右侧的参数部分
                    String line = new String(contentData,"ISO8859-1");
                    System.out.println("正文内容：" + line);
                    parseParameters(line);
                }
            }

        }

    }

    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        StringBuilder builder = new StringBuilder();
        char cur = 'a';//保存本次读取的字符
        char pre = 'a';//保存上次读取的字符
        int d = 0;
        while ((d = in.read()) != -1){
            //本次读取的字符
            cur = (char)d;
            //判断是否连续读到了回车符+换行符
            if (pre==13 && cur==10) {
                break;
            }
            builder.append(cur);
            //在下次循环之前将本次读取到的字符记在上次读取的字符
            pre = cur;
            //System.out.print((char) d);
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

    public String getHeader(String name){
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name){
        String value = parameters.get(name);
        //%E5%8C%85%E4%BD%B3%E5%A5%87
        if (value != null) {
            try {
                value = URLDecoder.decode(value,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return value;

    }
}
