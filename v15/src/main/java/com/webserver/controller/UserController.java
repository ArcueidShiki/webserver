package com.webserver.controller;

import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;

/**
 * controller 层 处理与用户相关的业务
 */
public class UserController {
    // 用户信息目录
    private static File userDir;
    static {
        userDir = new File("./users");
        if(!userDir.exists()) userDir.mkdirs();
    }
    public void reg(HttpServletRequest request, HttpServletResponse response){
        // 1.获取用户表单细腻些
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        String nickname = request.getParameters("nickname");
        String ageStr = request.getParameters("age");
        int age = Integer.parseInt(ageStr);
        System.out.println(username+","+password+","+nickname+","+ageStr);
        // 2.保存用户 obj 序列化
        File userFile = new File(userDir,username+".obj");
        try(FileOutputStream fos = new FileOutputStream(userFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            User user = new User(username,password,nickname,age);
            oos.writeObject(user);
            //注册成功
            File file = new File("webapps","/myweb/reg_success.html");
            response.setContentFile(file);
        }catch (IOException e){
            e.printStackTrace();
        }
        // 3.响应成功失败页面
    }
}
