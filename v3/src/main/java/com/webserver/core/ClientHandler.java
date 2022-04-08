package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
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
            String line = builder.toString().trim();
            System.out.println(line);
            String[] data = line.split("\\s");

            String method = data[0];
            String uri = data[1];
            String protocol = data[2];

            System.out.println("method:" +method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
