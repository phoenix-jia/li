package com.example.demo.web;


import com.example.demo.po.Type;
import com.example.demo.service.BlogsService;
import com.example.demo.service.TypeService;
import com.example.demo.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogsService blogsService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 8,direction = Sort.Direction.DESC) Pageable pageable,
                        Model model, @PathVariable Long id){
        List<Type> types = typeService.listInextType(1000);
        if (id == -1){   //刚打开type页面时没有选中的type，这时给个id=-1;
            id = types.get(0).getId();
        }
        BlogQuery blogquery = new BlogQuery();
        blogquery.setTypeId(id);
        model.addAttribute("types",types);
        model.addAttribute("page",blogsService.listBlog(pageable,blogquery));
        model.addAttribute("activeType",id);  //当前id
        return "types";
    }
}
