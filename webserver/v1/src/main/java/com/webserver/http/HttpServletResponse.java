package com.webserver.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 响应对象
 * 该类的每一个实例都表示一个响应，每个响应由三部分构成
 * 1.状态行
 * 2.响应头
 * 3.响应正文
 */
public class HttpServletResponse {

    //状态行相关信息
    private int statusCode = 200;
    private String statusReason = "OK";

    //响应头相关信息
    private Map<String,String> headers = new HashMap<>();

    //响应正文相关信息
    private File entity;//响应正文对应的实体文件

    //用来作为响应动态数据，作为正文使用
    private byte[] contentData;

    /*
    java.io.ByteArrayOutputStream
    该流是一个低级流，内部维护一个字节数组，通过这个流写出的数据，
    会全部存在在内部的字节数组中。
     */
    private ByteArrayOutputStream baos;

    private Socket socket;

    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }

    /**
     * 发送响应
     */
    public void response() throws IOException{
        sendBefore();
        //发送状态行
        sendStatusLine();
        //发送响应头
        sendHeaders();
        //发送响应正文
        sendContent();
        System.out.println("响应发送完毕！");
    }

    /**
     * 发送响应前的准备工作
     */
    public void sendBefore(){
        /*
           如果baos不为null，说明向该输出流写出了一组动态数据，
           将动态数据作为正文内容，因此需要将该流中的字节数组获取
         */
        if (baos != null) {
            contentData = baos.toByteArray();
            //自动添加响应头Content-Length
            addHeaders("Content-Length",String.valueOf(contentData.length));
        }

    }

    /**
     * 发送一行字符串
     */
    private void println(String line) throws IOException{
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes("ISO8859-1");
        out.write(data);
        out.write(13);//发送一个回车符
        out.write(10);//发送一个换行符

    }

    /**
     * 发送状态行
     */
    private void sendStatusLine() throws IOException {
        String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
        println(line);
    }

    /**
     * 发送响应头
     */
    private void sendHeaders() throws IOException{

        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for (Map.Entry<String,String> e : entrySet) {
            String key = e.getKey();
            String value = e.getValue();
            String line = key + ": " + value;
            println(line);
        }

        //单独发送CRLF回车符合换行符，表示响应头发送完毕
        println("");
    }

    /**
     * 发送响应正文
     */
    private void sendContent() throws IOException{
        OutputStream out = socket.getOutputStream();

        if (contentData != null) {
            out.write(contentData);
        } else if (entity != null) {
            try(FileInputStream fis = new FileInputStream(entity);){
                int len;
                byte[] buf = new byte[1024 * 10];
                while ((len = fis.read(buf)) != -1){
                    out.write(buf,0,len);
                }
            }

        }

    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        this.entity = entity;
        String fileName = entity.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        //根据后缀获取类型
        String type = HttpContext.getMimeType(ext);

        //添加两个响应头Content-Type和Content-Length
        addHeaders("Content-Type",type);
        addHeaders("Content-Length",String.valueOf(entity.length()));
    }

    /**
     * 添加一个需要发送的响应头
     * @param name
     * @param value
     */
    public void addHeaders(String name,String value){
        this.headers.put(name, value);
    }

    /**
     * 通过返回的PrintWriter写出的文本数据作为响应正文发送给客户端
     * @return
     */
    public PrintWriter getWriter() {
        OutputStream os = getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os,StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pw = new PrintWriter(bw,true);
        return pw;

//        return new PrintWriter(
//                new BufferedWriter(
//                        new OutputStreamWriter(
//                                getOutputStream(), StandardCharsets.UTF_8
//                        )
//                ),true
//        );
    }

    /**
     * 通过返回的字节输出流写出的字节，会被当做响应正文发送给客户端
     * @return
     */
    public OutputStream getOutputStream(){
        if (baos == null) {
            baos = new ByteArrayOutputStream();
        }
        return baos;
    }

    public void senContentType(String type){
        addHeaders("Content-Type",type);
    }


}
