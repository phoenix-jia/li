package com.example.demo.dao;

import com.example.demo.po.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type,Long> {
     Type findByName(String name);

     @Query("select t from Type t")  //自定义查询，按分页方式查询type，t为Type的别名,用来查询特定数量的type放到index中
     List<Type> findTop(Pageable pageable);
}
