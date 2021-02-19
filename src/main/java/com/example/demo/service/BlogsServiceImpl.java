package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.dao.BlogRepository;
import com.example.demo.po.Blog;
import com.example.demo.po.Type;
import com.example.demo.utils.MarkdownUtils;
import com.example.demo.utils.MyBeanUtils;
import com.example.demo.vo.BlogQuery;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class BlogsServiceImpl implements BlogsService {


    @Autowired
    private BlogRepository blogRepository;

    Counter counter;

    public BlogsServiceImpl(MeterRegistry meterRegistry){
        counter = meterRegistry.counter("BlogsServiceImpl.listBlog.count");
    }

    @Transactional
    @Override
    public Optional<Blog> getBlog(Long id) {
        return blogRepository.findById(id);
    }

    @Override
    public Blog getAndConvert(Long id) {
        Optional<Blog> b = blogRepository.findById(id);
        Blog blog;
        if (b.isPresent()){
            blog = b.get();
        }else{
            throw new NotFoundException("博客不存在");
        }
        Blog blog1 = new Blog();
        BeanUtils.copyProperties(blog,blog1);//不能直接blog.setcontent(),这样会改变数据库。
        String content = blog1.getContent();
        blog1.setContent(MarkdownUtils.markdownToHtmlExtensions(content));//改变contnet为html
        blogRepository.updateView(id);
        return blog1;
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        counter.increment();
        return blogRepository.findAll(new Specification<Blog>() {
            @Override    //root 要查询的对象，criteriaQuery 条件容器，criteriaBuilder查询工具
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(!"".equals(blog.getTitle()) && blog.getTitle() != null){
                    //title存在且不为空字符。如果条件成立就将title放到查询条件中
                    predicates.add(cb.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));//title对应Blog.class中的title属性
                    //前者是字段，后者是条件
                }
                if(blog.getTypeId() != null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                if(blog.isRecommend()){
                    predicates.add(cb.equal(root.<Boolean>get("isRecommend"),blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));  //根据条件个数size（），来拼接查询条件
                return null;
            }
        },pageable);
    }
    @Transactional
    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String,List<Blog>> map = new HashMap<>();
        for(String year : years){
            map.put(year,blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {  //编辑和新增公用一个saveblog方法
        if(blog.getId() == null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id,Blog blog) {
        Optional<Blog> blog1 = blogRepository.findById(id);
        if (!blog1.isPresent()){
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(blog,blog1.get(), MyBeanUtils.getNullPropertyNames(blog));  //第三个参数为需要去除的空属性的名字，以免每次更新讲views和createTime清空
        blog1.get().setUpdateTime(new Date());
        return blogRepository.save(blog1.get());
    }

    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public List<Blog> listRecommendBlog(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable = PageRequest.of(0,size,sort);
        return blogRepository.findTop(pageable);
    }
}
