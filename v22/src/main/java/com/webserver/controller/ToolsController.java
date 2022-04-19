package com.webserver.controller;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import qrcode.QRCodeUtil;


/**
 * 控制 二维码生成 工具类
 */
public class ToolsController {
    public void createQR(HttpServletRequest request, HttpServletResponse response){
        String content = request.getParameters("content");
        try {
            QRCodeUtil.encode(content,"bg.jpg",response.getOutputStream(),true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("image/jpeg");
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
}
