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
            int d;
            while((d = in.read()) != -1){
                System.out.print((char)d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
