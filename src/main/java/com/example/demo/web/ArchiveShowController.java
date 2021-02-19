package com.example.demo.web;
import com.example.demo.service.BlogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchiveShowController{
    @Autowired
    private BlogsService blogsService;

    @GetMapping("/archives")
    public String archives (Model model){
        model.addAttribute("archiveMap",blogsService.archiveBlog());
        model.addAttribute("blogCount",blogsService.countBlog());
        return "archives";
    }
}
