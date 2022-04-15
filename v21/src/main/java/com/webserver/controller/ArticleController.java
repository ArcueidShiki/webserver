package com.webserver.controller;

import com.webserver.entity.Article;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

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

}
