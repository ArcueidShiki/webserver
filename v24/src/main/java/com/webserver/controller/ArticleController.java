package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.entity.Article;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class ArticleController {
    private static File articleDir;
    private static Map<String,Integer> map = new HashMap<>();
    static {
        articleDir = new File("./articles");
        if(!articleDir.exists()) articleDir.mkdirs();
        File[] files = articleDir.listFiles();
        // ""空字符串substring出来是空
        for(File file:files){
            String name = file.getName();
            if(name.contains(").obj")){
                //存在同名文件
                //id 重复title文件
                int id = 1+Integer.parseInt(name.substring(name.lastIndexOf("(")+1,name.lastIndexOf(").obj")));
                map.put(name.substring(0,name.indexOf("(")),id);
            }else{
                // 没有同名文件
                map.put(name.substring(0,name.lastIndexOf(".")),0);
            }
        }
    }
    /**
     * 处理发表文章的方法
     */
    @RequestMapping("/myweb/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response){
        String title = request.getParameters("title");
        String author = request.getParameters("author");
        String body = request.getParameters("body");
        File article_fail = new File("webapps","/myweb/article_fail.html");
        if(title == null || author == null || body == null){
            response.setContentFile(article_fail);
            return;
        }
        File articleFile;
        // 如果map.get(key) 如果key不存在value返回null
        if(map.get(title) == null){
            // 没有同名文件
            articleFile = new File(articleDir,title+".obj");
            map.put(title,0);
        }else{
            // 对同名文件更新id
            int id = map.get(title)+1;
            articleFile = new File(articleDir,title+"("+id+").obj");
            map.put(title,id);
        }
        Article article = new Article(title,author,body);
        try(
                FileOutputStream fos = new FileOutputStream(articleFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                ){
            oos.writeObject(article);
        }catch (IOException e){
            e.printStackTrace();
        }
        //发布成功
        File file = new File("webapps","/myweb/article_success.html");
        response.setContentFile(file);

    }

    /**
     * 展示所有文章 title 作者
     */
    @RequestMapping("/myweb/showAllArticle")
    public void showAllArticle(HttpServletRequest request,HttpServletResponse response) {
        List<Article> articleList = new ArrayList<>();
        File[] subs = articleDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File file :subs){
            try(
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
                Article article = (Article)ois.readObject();
                articleList.add(article);
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        articleList.forEach(System.err::println);
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>文章列表</title>\n" +
                "    <style>\n" +
                "        *{\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        tr{\n" +
                "            background-color: #9f8989;\n" +
                "            box-shadow: #c0a16b;\n" +
                "        }\n" +
                "        .head{\n" +
                "            font-size: larger;\n" +
                "            font-weight: bold;\n" +
                "            color: #d43f3a;\n" +
                "        }\n" +
                "        td{\n" +
                "            width: 80px;\n" +
                "            text-align: center;\n" +
                "            border: solid 1px cornsilk;\n" +
                "            font-size: large;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .title{\n" +
                "            text-align: center;\n" +
                "            width: 300px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<center>\n" +
                "    <h1>文章列表</h1>\n" +
                "    <br>\n" +
                "    <table style=\"border: 1px solid #c0a16b\">\n" +
                "        <tr class=\"head\">\n" +
                "            <td>作 &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp者</td>\n" +
                "            <td style=\"text-align: center\">标&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp题</td>\n" +
                "        </tr>");
        for(Article article:articleList){
            pw.println("<tr>");
            pw.println("<td>"+article.getAuthor()+"</td>");
            pw.println("<td>"+article.getTile()+"</td>");
            pw.println("</tr>");
        }
        pw.println("</table>\n" +
                "</center>\n" +
                "</body>\n" +
                "</html>");
        response.setContentType("text/html");

    }
}
