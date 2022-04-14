package com.webserver.test;

import java.io.File;

public class GetName {
    public static void main(String[] args) {
        File file = new File("爱尔奎特.obj");
        String fileName = file.getName();
        String copy = "爱尔奎特(1).obj";
        System.out.println("fileName = " + fileName);
        int lastIndex = copy.lastIndexOf(".obj");
        int copyNum = Integer.parseInt(copy.substring(4+1,lastIndex-1));
        ++copyNum;

        System.out.println("lastIndex = " + lastIndex);
        System.out.println("copyNum = " + copyNum);

        System.out.println("\"\".substring(0,0) = " + "".substring(0, 0));

    }
}
