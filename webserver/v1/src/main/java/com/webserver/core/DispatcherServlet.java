package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.controller.UserController;
import com.webserver.http.HttpContext;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import com.webserver.vo.User;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类用于完成请求的处理工作
 */
public class DispatcherServlet {
    /*
     单例模式：23种设计模式之一，属于创建型模型
     应用场景：当一个类需要一个实例时，可以使用该模式。
     分析：当前DispatcherServlet类中并没有属性，可以理解为是无状态(没特征)，
          可以让对象创建一个都是同一个对象，因为不需要通过特征进行区分是否是不同的实例，
          当前可以通过单例设计模式进行完成逻辑
     思考：需要解决外界调用不能使用new关键字调用构造器的行为，原因为每次使用new运算符调用
          构造器都会进行创建一个新的实例
     单例使用步骤：
               1.提供静态的私有的当前类型的属性
               2.私有化构造器
               3.提供静态的公开方法，外界可以调用获取当前类的实例的方法
     */
    private static DispatcherServlet obj;

    private DispatcherServlet() {

    }

    public static DispatcherServlet getInstance() {
        if (obj == null) {
            obj = new DispatcherServlet();
        }
        return obj;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String path = request.getRequestURI(); //  /myweb/reg    /myweb/index.html
        System.out.println("请求的抽象路径：" + path);
        //处理业务请求
        HandlerMapping.MethodMapping mm = HandlerMapping.getRequestMapping(path);

        if (mm != null) {
            Object controller = mm.getController();  //UserController
            Method method = mm.getMethod();   //  reg()
            Object returnValue = method.invoke(controller,request,response);

            path = null;
            //方法返回值不是void
            if (method.getReturnType() != void.class) {
                if (returnValue != null) {
                    path = returnValue.toString();   //path = /myweb/reg_success.html
                }
            }

        }

        if (path != null) {  //  /myweb/index.html
            //判断请求是否为请求资源
            File file = new File("webapps" + path);
            if (file.isFile()) {//如果是文件
                response.setEntity(file);

            } else {//如果不是文件(要么不存在，要么是目录)
                file = new File("webapps/root/404.html");
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setEntity(file);
            }
        }


        //统一添加一个响应头，主要做测试使用，这个头就是告诉浏览器我是谁(webserver)
        response.addHeaders("Server", "webserver");
    }
}
