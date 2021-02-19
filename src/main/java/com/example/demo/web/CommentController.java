package com.example.demo.web;


import com.example.demo.po.Comment;
import com.example.demo.po.User;
import com.example.demo.service.BlogsService;
import com.example.demo.service.CommentServices;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class CommentController {

    public final CommentServices commentServices;

    public final BlogsService blogsService;

    @Value("${comment.avatar}")  //拿到application.yml中的字符串
    private  String avatar;

    public CommentController(CommentServices commentServices, BlogsService blogsService) {
        this.commentServices = commentServices;
        this.blogsService = blogsService;

    }

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model){
        model.addAttribute("comments",commentServices.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }
    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session){
        Long blogId = comment.getBlog().getId();
        if (blogsService.getBlog(blogId).isPresent()){
            comment.setBlog(blogsService.getBlog(blogId).get());  //前台只传递了blog.id
        }
        User user = (User)session.getAttribute("user");
        if (user != null){
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
//            comment.setNickname(user.getNickname());
        }else{
            comment.setAvatar(avatar);
        }
        comment.setAvatar(avatar);
        commentServices.saveComment(comment);
        return "redirect:/comments/"+comment.getBlog().getId();
    }
}
