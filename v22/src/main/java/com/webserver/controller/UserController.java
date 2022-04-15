package com.webserver.controller;

import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 展示用户列表动态页面方式
     * @param response
     */
    public void showAll(HttpServletResponse response) {
        List<User> userList = new ArrayList<>();
        // 可能会有隐藏文件。需要过滤
        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File userFile : subs){
            try( FileInputStream fis = new FileInputStream(userFile);
                 ObjectInputStream ois = new ObjectInputStream(fis);
                ){
               User user = (User)ois.readObject();
               userList.add(user);
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        userList.forEach(System.err::println);
        /**
         * Java 中所有的对象都在内存里。 ByteArrayOutputStream
         * 将数据以字节数组的形式存储在 baos里 就不需要 在硬盘上生成html文件了
         * 用PrintWriter动态页面写出给Client
         */
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>用户列表</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<center>\n" +
                "<h1>用户列表</h1>\n" +
                "<table border=\"1\">\n" +
                "<tr>\n" +
                "<td>用户名</td>\n" +
                "<td>密码</td>\n" +
                "<td>昵称</td>\n" +
                "<td>年龄</td>\n" +
                "</tr>");
        for (User u : userList) {
            pw.println("<tr>");
            pw.println("<td>" + u.getUsername() + "</td>");
            pw.println("<td>" + u.getPassword() + "</td>");
            pw.println("<td>" + u.getNickname() + "</td>");
            pw.println("<td>" + u.getAge() + "</td>");
            pw.println("</tr>");
        }
        pw.println("</table>\n" +
                "</center>\n" +
                "</body>\n" +
                "</html>");
        /** *********************************************************
         * 1. 【性能】：如果把html文件写入硬盘，这里效率和性能是很慢把。创建本地html的过程是无必要的
         * 2. 【并发】：而且 多线程状态下。在同一文件中写东西。并发不安全
         */
//        pw.flush();
        //添加响应头Content-Type
        response.setContentType("text/html");
    }
}
