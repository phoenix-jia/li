package com.example.demo.service;

import com.example.demo.po.Comment;
import org.springframework.stereotype.Service;


import java.util.List;

public interface CommentServices {
    List<Comment> listCommentByBlogId(Long blogId);
    Comment saveComment(Comment comment);
}
