package com.example.demo.service;
import com.example.demo.po.Blog;
import com.example.demo.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface BlogsService {

    Optional<Blog> getBlog(Long id);

    Blog getAndConvert(Long id); //将数据库中content由字符串转换成html

    Page<Blog> listBlog(Pageable pageable, BlogQuery blog);

    Page<Blog> listBlog(Pageable pageable);

    Page<Blog> listBlog(String query,Pageable pageable);

    Map<String,List<Blog>> archiveBlog();

    Long countBlog();

    Blog  saveBlog(Blog blog);

    Blog updateBlog(Long id,Blog blog);

    void deleteBlog(Long id);

    List<Blog> listRecommendBlog(Integer size);

}
