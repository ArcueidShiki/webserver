package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * socket获取输入流读取客户端发送来的一行字符串
     * @return
     */
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        char pre = 'a',cur = 'a';
        StringBuilder builder = new StringBuilder();
        int d;
        while((d = in.read()) != -1){
            cur = (char)d;
            if(pre == 13 && cur == 10) break; // 13 CR 10 LF
            builder.append(cur);
            pre = cur;
        }
        return builder.toString().trim();
    }

    @Override
    public void run() {
        try {
            String line = readLine();
            System.out.println(line);
            String[] data = line.split("\\s");

            String method = data[0];
            String uri = data[1];
            String protocol = data[2];

            System.out.println("method:" +method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);

            //解析消息头
            Map<String, String> headers = new HashMap<>();
            while(true){
                line = readLine();
                //此处要判断isEmpty 不能为null
                if(line.isEmpty()) break;
                System.out.println("消息头:"+line);
                headers.put(line.split(":")[0],line.split(":")[1]);
            }
            //将消息头按照冒号空格拆分为消息头的名字 和值，key，value存入Headers.
            System.out.println("headers"+headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
