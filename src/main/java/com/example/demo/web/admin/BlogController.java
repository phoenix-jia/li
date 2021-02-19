package com.example.demo.web.admin;


import com.example.demo.po.Blog;
import com.example.demo.po.User;
import com.example.demo.service.BlogsService;
import com.example.demo.service.TagService;
import com.example.demo.service.TypeService;
import com.example.demo.vo.BlogQuery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-new";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

//    变量表达式： ${...}，是获取容器上下文变量的值.
//    选择变量表达式： *{...}，获取指定的对象中的变量值。如果是单独的对象，则等价于${}。
//    消息表达式： #{...}表达式与th:text一起使用，加载数据源中的消息，用于国际化
//    链接网址表达式： @{...}，获取网址链接

    private final BlogsService blogsService;
    private final TagService tagService;
    private final TypeService typeService;
    public BlogController(BlogsService blogsService, TypeService typeService, TagService tagService) {
        this.blogsService = blogsService;
        this.typeService = typeService;
        this.tagService = tagService;
    }


    @GetMapping("/blogs")  //第一次访问blogs，给前台传递所有查到的值
    public String blogs(@PageableDefault(size = 2,direction = Sort.Direction.DESC) Pageable pageable,
                        Model model ,BlogQuery blog){
        model.addAttribute("types",typeService.listType()); //查询所有类型
        model.addAttribute("page",blogsService.listBlog(pageable,blog)); //查询所有blog
        return LIST;
    }


    @PostMapping("/blogs/search")  //这里提交form表单，包含筛选条件
    public String search(Model model , @PageableDefault(size = 2,direction = Sort.Direction.DESC,sort = {"updateTime"}) Pageable pageable, BlogQuery blog){
        model.addAttribute("page",blogsService.listBlog(pageable,blog));
        return "admin/blogs :: blogList";   //前端table定义了blogList块，使用ajax只刷新这部分
    }


    @GetMapping("/blogs/input")  //传递blog对象给blog-new前台，前台修改后再传回来saveBlog，所以修改后传递回来的对象类型为Blog
    public String input(Model model){
        setTypeAndTag(model);

        model.addAttribute("blog",new Blog());
        return INPUT;
    }


    @GetMapping("/blogs/{id}/input")  //传递blog对象给blog-new前台，前台修改后再传回来saveBlog，所以修改后传递回来的对象类型为Blog
    public String editinput(@PathVariable Long id, Model model){
        setTypeAndTag(model);
        Optional<Blog> blog = blogsService.getBlog(id);
        if(blog.isPresent()){
            blog.get().init();
            model.addAttribute("blog",blog.get());
        }
        return INPUT;
    }

    private void setTypeAndTag(Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }

    @PostMapping("/blogs")  //这里是新增和编辑blog公用一个post方法，使用blog.getId是否存在来进行区分。
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session){

        blog.setUser((User)session.getAttribute("user"));

        if (typeService.getType(blog.getType().getId()).isPresent()){
            blog.setType(typeService.getType(blog.getType().getId()).get());//前台向后台传递的是type.id，这里再查询后执行set方法
        }
        blog.setTags(tagService.listTag(blog.getTagIds()));  //前端只给了一个value组成的字符串，需要转换成数组
        Blog b;
        if (blog.getId() == null){                            //判断前端传递的是新增还是修改的post
             b = blogsService.saveBlog(blog);
        }else{
            b=blogsService.updateBlog(blog.getId(),blog);
        }
        if(b == null){
            attributes.addFlashAttribute("message","新增失败");
        }else{
            attributes.addFlashAttribute("message","新增成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        blogsService.deleteBlog(id);
        attributes.addFlashAttribute("message","删除成功");
        return REDIRECT_LIST;
    }
}
