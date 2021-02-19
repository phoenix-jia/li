package com.example.demo.web.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AboutController {

    @GetMapping("/about")
    public String about(){  //要实现加载个人信息的功能
        return "admin/about";
    }
    @PostMapping("/upload")
    public String fileUpload(@RequestParam("email") String email,
                             @RequestParam("name") String name,
                             @RequestParam("headImg") MultipartFile headImg,
                             @RequestParam("photos") MultipartFile[] photos) throws IOException {
        log.info("上传文件为：email={},name={},headImg={},photos={}",email,name,headImg.getSize(),photos.length);
        if (!headImg.isEmpty()){
            String filename = headImg.getOriginalFilename();
            headImg.transferTo(new File("G:\\cache\\"+filename));
        }

        if (photos.length>0){
            for (MultipartFile photo : photos){
                if (!photo.isEmpty()){
                    String filename = photo.getOriginalFilename();
                    photo.transferTo(new File("G:\\cache\\"+filename));
                }
            }
        }
        //默认限制单文件1M，多文件10M，超出会报错，已在yml修改
        return "admin/about";
    }
}
