package com.example.demo.service;

import com.example.demo.po.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TypeService {

    Type saveType(Type type);

    Type getTypeByName(String name);

    Optional<Type> getType(Long id);

    Page<Type> listType (Pageable pageable);

    List<Type> listInextType(Integer size);

    List<Type> listType();

    Type updateType(Long id , Type type);

    void delete (Long id);
}
