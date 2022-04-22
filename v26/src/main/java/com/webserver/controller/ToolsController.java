package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import qrcode.QRCodeUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


/**
 * 控制 二维码生成 工具类
 * 验证码：随机画图
 */
@Controller
public class ToolsController {
    @RequestMapping("/myweb/createQR")
    public void createQR(HttpServletRequest request, HttpServletResponse response){
        String content = request.getParameters("content");
        try {
            QRCodeUtil.encode(content,"bg.jpg",response.getOutputStream(),true);
            response.setContentType("image/jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 二维码相关测试
     */
    @RequestMapping("/myweb/random.jpg")
    public void createRandomImage(HttpServletRequest request,HttpServletResponse response){
        System.err.println("拦截到生成验证码的请求，进入创建验证码的方法");
        //1. 创建空图片 指定宽高
        BufferedImage image = new BufferedImage(70,30,BufferedImage.TYPE_INT_BGR);
        //2. 根据图片获取画笔，通过该画笔的画的内容都画到该图片上
        Graphics pen = image.getGraphics();
        //3. 生成4位验证码 combination of alpha number
        String line = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //4. 为图片填充一个随机颜色
        Random random = new Random();
        Color bgcolor = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
        //将画笔设置为该颜色
        pen.setColor(bgcolor);
        //填充 整张图片为画笔当前颜色
        pen.fillRect(0,0,70,70);
        for (int i = 0 ;i < 4 ;i++) {
            // 向图片上画4个字符
            String str = line.charAt(random.nextInt(line.length()))+"";
            //生成随机颜色
            Color color = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
            pen.setColor(color);
            //设置字体
            pen.setFont(new Font(null,Font.BOLD,21));
            pen.drawString(str,5+i*15,18+random.nextInt(10)-5);
        }
        // 随机生成4条干扰线
        for (int i = 0; i < 4; i++) {
            Color color = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
            pen.setColor(color);
            pen.drawLine(random.nextInt(71),random.nextInt(31),random.nextInt(71),random.nextInt(31));

        }
        //5. 【将图片写入文件】来生成该图片文件
        try {
            // para1 image, para2 format, para3 path location把 生成的图片 通过输出流发送出去
            ImageIO.write(image,"jpg",response.getOutputStream());
            response.setContentType("image/jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        /**
//         * 二维码中只能包含字符串
//         */
//        String message = "www.baidu.com";
//        try {
//            // args1 二维码上的文本信息
//            // args2 图片生成的位置
//            // args3 图片生成后通过流写出
//            // args4 是否压缩
////            QRCodeUtil.encode(message,"./arc.jpg");
////            QRCodeUtil.encode(message,new FileOutputStream("./arcueid.jpg"));
////            QRCodeUtil.encode(message,"bg.jpg","./qr.jpg",true);
//            QRCodeUtil.encode(message,"qr.jpg",new FileOutputStream("./qr1.jpg"),true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * 验证码 相关测试
     */
    public static void main(String[] args) {
        //1. 创建空图片 指定宽高
        BufferedImage image = new BufferedImage(70,30,BufferedImage.TYPE_INT_BGR);
        //2. 根据图片获取画笔，通过该画笔的画的内容都画到该图片上
        Graphics pen = image.getGraphics();
        //3. 生成4位验证码 combination of alpha number
        String line = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //4. 为图片填充一个随机颜色
        Random random = new Random();
        Color bgcolor = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
        //将画笔设置为该颜色
        pen.setColor(bgcolor);
        //填充 整张图片为画笔当前颜色
        pen.fillRect(0,0,70,70);
        for (int i = 0 ;i < 4 ;i++) {
            // 向图片上画4个字符
            String str = line.charAt(random.nextInt(line.length()))+"";
            //生成随机颜色
            Color color = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
            pen.setColor(color);
            //设置字体
            pen.setFont(new Font(null,Font.BOLD,21));
            pen.drawString(str,5+i*15,18+random.nextInt(10)-5);
        }
        // 随机生成4条干扰线
        for (int i = 0; i < 4; i++) {
            Color color = new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
            pen.setColor(color);
            pen.drawLine(random.nextInt(71),random.nextInt(31),random.nextInt(71),random.nextInt(31));

        }
        //5. 【将图片写入文件】来生成该图片文件
        try {
            // para1 image, para2 format, para3 path location
            ImageIO.write(image,"jpg",new FileOutputStream("./random.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
