package com.wind.person.controller;

import com.google.gson.Gson;
import com.wind.person.domain.Person;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lilandong
 * @date 2017/11/13
 */
@Controller
public class MvcController {

    /**
     * http://127.0.0.1:8080/index
     * 跳转index.html，展示thymeleaf的使用
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request,Model model) {
        Person single = new Person(Long.valueOf(1),"addr1",1,"name1");
        List<Person> people = new ArrayList<Person>();
        Person p1 = new Person(Long.valueOf(2),"addr2",2,"name2");
        Person p2 = new Person(Long.valueOf(3),"addr3",3,"name3");
        Person p3 = new Person(Long.valueOf(4),"addr4",4,"name4");
        people.add(p1);
        people.add(p2);
        people.add(p3);
        model.addAttribute("singlePerson", single);
        model.addAttribute("people", people);

        request.setAttribute("requestMessage", "requestScope");
        request.getSession().setAttribute("sessionMessage", "sessionScope");
        request.getServletContext().setAttribute("applicationMessage","applicationScope");
        return "index";
    }

    /**
     *  http://127.0.0.1:8080/json
     *  返回jsonView，自动找到这个bean，然后进行试图渲染
     */
    @RequestMapping(value = "/json", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String json(Model model) {
        Person single = new Person(Long.valueOf(5),"addr5",5,"name5");
        model.addAttribute("single", single);
        return "jsonView";
    }

    /**
     * springboot与ajax的交互
     */
    @RequestMapping(value = "/savePerson2ajax")
    @ResponseBody
    public String savePerson2ajax(Person person) {
        Gson gson = new Gson();
        return gson.toJson(person);
    }


    /**
     * 文件上传具体实现方法（单文件上传）
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // 这里只是简单例子，文件直接输出到项目路径下。
                // 实际项目中，文件需要输出到指定位置，需要在增加代码处理。
                // 还有关于文件格式限制、文件大小限制，详见：中配置。
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(file.getOriginalFilename())));
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }
            return "上传成功";
        } else {
            return "上传失败，因为文件是空的.";
        }
    }

    /**
     * 多文件上传 主要是使用了MultipartHttpServletRequest和MultipartFile
     */
    @RequestMapping(value = "/upload/batch", method = RequestMethod.POST)
    public @ResponseBody String batchUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file;
        BufferedOutputStream stream;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    return "You failed to upload " + i + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + i + " because the file was empty.";
            }
        }
        return "upload successful";
    }
}
