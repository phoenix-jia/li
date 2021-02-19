package com.example.demo.dao;

import com.example.demo.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long>, JpaSpecificationExecutor<Blog> {


    @Query("select b from Blog b where b.isRecommend = true")
    List<Blog> findTop(Pageable pageable);

    //select * from t_blog where title like '%内容%'
    @Query("select b from Blog b where b.title like ?1 or b.content like ?1")//?1 第一个参数
    Page<Blog> findByQuery(String query, Pageable pageable);


    @Transactional  //更新必须有，或者service调用时加这个
    @Modifying   //更新需要这个
    @Query("update Blog b set b.views = b.views+1 where b.id = ?1")
    int updateView(Long id);

    @Query("select function('date_format',b.updateTime,'%Y') as year from Blog b group by year order by function('date_format',b.updateTime,'%Y') desc") //
    //select date_format(b.update_time,'%Y') as year from t_blog b GROUP by year ORDER BY year DESC;
    List<String> findGroupYear();  //查询所有的年份

    @Query("select b from Blog b where function('date_format',b.updateTime,'%Y') = ?1 ") //查询对应年份blog
    List<Blog> findByYear(String year);
}
