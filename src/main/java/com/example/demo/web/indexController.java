package com.example.demo.web;

import com.example.demo.service.BlogsService;
import com.example.demo.service.TagService;
import com.example.demo.service.TypeService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class indexController {

    private final BlogsService blogsService;
    private final TypeService typeService;
    private final TagService tagService;
    public indexController(BlogsService blogsService, TypeService typeService,TagService tagService) {
        this.blogsService = blogsService;
        this.typeService = typeService;
        this.tagService = tagService;
    }

    @GetMapping("/")
    public String index(@PageableDefault(size = 2,direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){
        model.addAttribute("page",blogsService.listBlog(pageable));
        model.addAttribute("types",typeService.listInextType(6));
        model.addAttribute("tags",tagService.listIndexTag(10));
        model.addAttribute("recommendBlogs",blogsService.listRecommendBlog(8));
        return "index";
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size = 2,direction = Sort.Direction.DESC,sort = {"updateTime"}) Pageable pageable,
                         Model model, @RequestParam String query){
        model.addAttribute("page",blogsService.listBlog("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model){
        model.addAttribute("blog",blogsService.getAndConvert(id));
        return "blog";
    }
}
