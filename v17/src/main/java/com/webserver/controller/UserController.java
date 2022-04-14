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
    public void login(HttpServletRequest request,HttpServletResponse response){
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        File login_error = new File("webapps","/myweb/login_error.html");
        if(username == null || password == null) {
            response.setContentFile(login_error);
            return;
        }
        File userFile = new File(userDir,username+".obj");
        if(!userFile.exists()){
            response.setContentFile(login_error);
            return;
        }
        try(
                FileInputStream fis = new FileInputStream(userFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ){
            User user = (User)ois.readObject();
            // 用户名肯定存在且正确 只验证密码
            if(!password.equals(user.getPassword())){
                response.setContentFile(login_error);
                return;
            }
            File file = new File("webapps","/myweb/login_success.html");
            response.setContentFile(file);
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public void reg(HttpServletRequest request, HttpServletResponse response){
        // 1.获取用户表单细腻些
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        String nickname = request.getParameters("nickname");
        String ageStr = request.getParameters("age");
        /*
        必要信息验证：
        四项不能为null，age必须是一个数字。通过正则表达
        否则响应注册失败页面 reg_input_error.html
         */
        String ageRegex = "\\d{0,3}";
        if(username == null || password == null || nickname == null || ageStr == null || !ageStr.matches(ageRegex)) {
            File file = new File("webapps","/myweb/reg_input_error.html");
            response.setContentFile(file);
            return;
        }
        int age = Integer.parseInt(ageStr);
        System.out.println(username + "," + password + "," + nickname + "," + ageStr);
        // 2.保存用户 obj 序列化
        File userFile = new File(userDir, username + ".obj");
        /*
        判断用户信息是否存在
         */
        if(userFile.exists()){
            File file = new File("webapps","/myweb/have_user.html");
            response.setContentFile(file);
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(userFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            User user = new User(username, password, nickname, age);
            oos.writeObject(user);
            //注册成功
            File file = new File("webapps", "/myweb/reg_success.html");
            response.setContentFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 3.响应成功失败页面
    }
}
