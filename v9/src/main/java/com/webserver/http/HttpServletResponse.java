package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServletResponse {
    private Socket socket;
    private int statusCode = 200;
    private String statusReason = "OK";

    private File contentFile;

    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }

    /**
     * 发送响应包括：状态行+响应头+响应正文
     */
    public void response() throws IOException{
        sendStatusLine();
        sendHeaders();
        //响应正文
        sendContent(contentFile);
    }

    private void sendHeaders() throws IOException {
        println("Content-Type: text/html");
        println("Content-Length: "+contentFile.length()); //响应头
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
    public void sendContent(File file) throws IOException {
        OutputStream out = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[1024 * 10];
        int len;
        while ((len = fis.read(data)) != -1) {
            out.write(data, 0, len);
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
    }
}