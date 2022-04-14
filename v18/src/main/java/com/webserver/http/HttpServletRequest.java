package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求对象
 * 该类的每一个实例用于表示一个HTTP请求内容
 * 每个请求由三部分构成:
 * 请求行，消息头，消息正文
 */
public class HttpServletRequest {
    private Socket socket;
    //请求行相关信息
    private String method;//请求方式
    private String uri;//抽象路径
    private String protocol;//协议版本
    private String requestURI; // accept left part of ? that is abstract path;
    private String queryString; // receive right part of ? that is parameters;
    private Map<String,String> parameters = new HashMap<>(); // receive the submitted information include name and value from client;
    //消息头相关信息
    private Map<String,String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1.1解析请求行
        parseRequestLine();
        //1.2解析消息头
        parseHeaders();
        //1.3解析消息正文
        parseContent();
    }

    /**
     * 解析请求行
     * parse:解析
     */
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        if(line.isEmpty()) throw new EmptyRequestException("空请求");
        System.out.println(line);

        String[] array = line.split("\\s");
        method = array[0];
        uri = array[1];//这里可能会出现数组下标越界异常!原因:浏览器空请求，后期会解决
        protocol = array[2];
        //进一步解析uri。
        parseUri(uri);
        System.out.println("method:"+method);    //GET
        System.out.println("uri:"+uri);          // /myweb/index.html
        System.out.println("protocol:"+protocol);//HTTP/1.1
    }

    /**
     * 进一步解析uri
     */
    private void parseUri(String uri){
        // 1.判断uri是否含有？
        if(uri.contains("?")) {
            String[] data = uri.split("\\?");
                requestURI = data[0];
            // ? 号右边有东西
            if(data.length > 1) {
                queryString = data[1];
                parseParameter(queryString);
            }
        }else{
            requestURI = uri;
        }
        System.out.println("requestURI: "+requestURI);
        System.out.println("queryString: "+queryString);
        System.out.println("parameters: "+parameters);
    }

    private void parseParameter(String line) {
        String[] data = line.split("&");
        for (int i = 0; i < data.length; i++) {
            String[] para = data[i].split("=");
            parameters.put(para[0],para.length > 1? para[1]:null);
        }
    }

    /**
     * 解析消息头
     */
    private void parseHeaders() throws IOException {
        while(true) {
            String line = readLine();
            if(line.isEmpty()){
                break;
            }
            System.out.println("消息头:" + line);
            //将消息头按照"冒号空格"拆分为消息头的名字和值并以key,value存入headers
            String[] array = line.split(":\\s");
            headers.put(array[0],array[1]);
        }
        System.out.println("headers:"+headers);
    }
    /**
     * 当请求方式是【 POST】时，说明包含消息正文
     * 解析消息正文 10101010100111......
     */
    private void parseContent(){
        if("post".equalsIgnoreCase(method)){
            if(headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                System.err.println(">>>>>>>>>>>>>>>>>"+contentLength);
                byte[] data = new byte[contentLength];
                try {
                    InputStream in = socket.getInputStream();
                    //将正文内容读进data中
                    in.read(data);
                    // 根据 Content-Type判断正文类型进行解析
                    if(headers.containsKey("Content-Type")){
                        String contentType = headers.get("Content-Type");
                        System.err.println(">>>>>>>>>>>>>>>>>"+contentType);
                        if("application/x-www-form-urlencoded".equals(contentType)){
                            String line = new String(data,"ISO8859-1");
                            System.err.println("POST提交过来的正文内容: "+line);
                            parseParameter(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * 通过socket获取的输入流读取客户端发送过来的一行字符串
     * @return
     */
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        char pre='a',cur='a';//pre上次读取的字符，cur本次读取的字符
        StringBuilder builder = new StringBuilder();
        int d;
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
    public String getHeader(String name){
        return headers.get(name);
    }

    /**
     * 根据参数名获取对应的参数值
     * @return
     */
    public String getParameters(String name) {
        return parameters.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

}
