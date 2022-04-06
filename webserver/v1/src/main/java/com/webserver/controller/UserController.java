package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import com.webserver.vo.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理与用户相关请求的操作
 */
@Controller
public class UserController {

    //定义用于保存注册用户信息文件的目录
    private static File userDir;

    static {
        userDir = new File("users");
        if (!userDir.exists()) {
            //如果users目录不存在创建该目录
            userDir.mkdirs();
        }
    }

    /**
     * 处理用户注册操作的请求
     */
    @RequestMapping("/myweb/reg")
    public String reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始处理注册...");
        //1.获取用户在注册页面中填写的注册信息
        //注意：getParameter方法中传递参数需要与注册表单输入框的名字一致
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username + "," + password + "," + nickname + "," + ageStr);

        /*
        严谨性验证：如果用户在注册表单中没有填写注册信息，则响应注册失败页面
        1、要求四个注册输入框中必须输入信息，不能最终服务端接收的数据为null
        2、年龄应该满足数字格式
        3、注册失败以后，应该允许用于可以重新注册
         */
        if (username == null || password == null || nickname == null
                || ageStr == null || !ageStr.matches("[0-9]+")) {

            return "/myweb/reg_info_error.html";
        }

        //2.将该注册用户信息保存
        //将当前注册的信息以一个User对象的形式进行序列化到：用户名.obj文件中,示例：users/baojq.obj
        File userFile = new File(userDir, username + ".obj");
        //如果文件存在则表示用户已经注册过
        if (userFile.exists()) {

            return "/myweb/reg_have_user.html";
        }


        Integer age = Integer.parseInt(ageStr);
        User user = new User(username, password, nickname, age);
        try (FileOutputStream fos = new FileOutputStream(userFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            //将注册的用户对象进行序列化操作
            oos.writeObject(user);
            System.out.println("写出注册信息完毕！！！");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.设置响应对象给用户回馈注册结果
        System.out.println("注册处理完毕！！！");
        return "/myweb/reg_success.html";
    }

    /**
     * 处理用户登录操作的请求
     */
    @RequestMapping("/myweb/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始处理登录...");
        //1.接收用户在登录页面中所填写的登录信息
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("username:" + username + "," + "password:" + password);
        if (username == null || password == null) {

            return "/myweb/login_error.html";
        }

        //2.对象反序列化，进行将填写登录表单的数据和已注册的用户进行比对
        File userFile = new File(userDir, username + ".obj");
        if (userFile.exists()) {
            //如果该用户注册过，进行用户信息比对
            try (
                    FileInputStream fis = new FileInputStream(userFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User) ois.readObject();
                //比对密码
                if (user.getPassword().equals(password)) {
                    //密码一致，登录成功

                    return "/myweb/login_success.html";
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        //处理登录失败
        System.out.println("登录处理完毕！");
        return "/myweb/login_fail.html";
    }

    /**
     * 处理用户查询操作的请求
     */
    @RequestMapping("/myweb/showAllUser")
    public void showAllUser(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始生成动态页面...");
        //1.读取users目录中所有的用户信息
        List<User> userList = new ArrayList<>();
        File[] subs = userDir.listFiles(f -> f.getName().endsWith(".obj"));
        for (File userFile : subs) {
            try (
                    FileInputStream fis = new FileInputStream(userFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User) ois.readObject();
                userList.add(user);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        userList.forEach(user -> System.out.println(user));
        //2.将用户信息拼接在一个html页面中

        PrintWriter pw = response.getWriter();

        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>用户列表</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<center>");
        pw.println("<h1>用户列表</h1>");
        pw.println("<table border=\"1\">");
        pw.println("<tr>");
        pw.println("<td>用户名</td>");
        pw.println("<td>密码</td>");
        pw.println("<td>昵称</td>");
        pw.println("<td>年龄</td>");
        pw.println("</tr>");


        for (User u : userList) {
            pw.println("<tr>");
            pw.println("<td>" + u.getUsername() + "</td>");
            pw.println("<td>" + u.getPassword() + "</td>");
            pw.println("<td>" + u.getNickname() + "</td>");
            pw.println("<td>" + u.getAge() + "</td>");
            pw.println("</tr>");
        }

        pw.println("</table>");
        pw.println("</center>");
        pw.println("</body>");
        pw.println("</html>");

        //添加响应头Content-Type
        response.senContentType("text/html");

        System.out.println("写出完毕！！！");

        //3.将页面作为正文响应给浏览器
        System.out.println("动态页面生成完毕！");
    }
}
