package com.example.demo.dao;

import com.example.demo.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByBlogIdAndParentCommentNull(Long blogId , Sort sort);  //方法名字自带查询效果，不用写具体实现方法
}
