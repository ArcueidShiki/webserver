package com.webserver.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpServletResponse {
    private Socket socket;
    private int statusCode = 200;
    private String statusReason = "OK";

    private File contentFile;
    //响应头
    private Map<String,String> headers = new HashMap<>();
    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }
    /*
    java.io.ByteArrayOutputStream
    该流是一个低级流，内部维护一个字节数组，通过这个流写出的数据，
    会全部存在在内部的字节数组中。
     */
    private static ByteArrayOutputStream baos;
    /**
     * 发送响应包括：状态行+响应头+响应正文
     */
    public void response() throws IOException{
        sendStatusLine();
        sendHeaders();
        //响应正文
        sendContent();
    }

    private void sendHeaders() throws IOException {
//        println("Content-Type: text/html");
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for(Map.Entry<String,String> kv:entrySet){
            println(kv.getKey()+": "+kv.getValue());
        }
        println("");
    }

    private void sendStatusLine() throws IOException {
        println("HTTP/1.1 "+statusCode+" "+statusReason); //状态行
    }

    /**
     * 发送一行字符串 以ISO8859-1编码。自动添加CR LF。
     * @param line
     * @throws IOException
     */
    public void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13); // CR
        out.write(10); // LF
    }
    public void sendContent() throws IOException {
        if (contentFile != null) {
            OutputStream out = socket.getOutputStream();
            //自动关闭流
            try(FileInputStream fis = new FileInputStream(contentFile)){
                byte[] data = new byte[1024 * 10];
                int len;
                while ((len = fis.read(data)) != -1) {
                    out.write(data, 0, len);
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

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
        String contentType = null;
        try {
            //根据静态资源的具体类型 发送响应
            contentType = Files.probeContentType(contentFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("异常检测："+contentType);
        if (contentType != null) {
            // 可变 headers Content-Type不能写死。
            addHeaders("Content-Type",contentType);
        }
        addHeaders("Content-Length",Long.toString(contentFile.length()));

    }

    //把一对响应头添加到headers的map里 包括 Content-Type+ Content-Length
    public void addHeaders(String name,String value){
        headers.put(name,value);
    }

    /**
     * 返回pw对象 向Client写出动态页面
     * @return
     */
    public PrintWriter getWriter(File file ) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter osw = new OutputStreamWriter(os,StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pw = new PrintWriter(bw,true);
        return pw;
    }

    /**
     * 通过返回的字节输出流写出的【字节数组】， 当作响应正文发送给客户端，pw只用一份
     * @return
     */
    private OutputStream getOutputStream() {
        if(baos == null) {
            baos = new ByteArrayOutputStream();
        }
        return baos;
    }
}
