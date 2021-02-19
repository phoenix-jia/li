package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.dao.TypeRepository;
import com.example.demo.po.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class TypeServiceImpl implements TypeService{

    private final TypeRepository typeRepository;

    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }
    @Transactional
    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    @Override
    public Type getTypeByName(String name) {
        Type type = typeRepository.findByName(name);
        return type;
    }

    @Transactional
    @Override
    public Optional<Type> getType(Long id){
        return typeRepository.findById(id);
    }


    @Transactional
    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public List<Type> listInextType(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size"); //拥有某条type的blog的数量,按照这个排倒序
        Pageable pageable = PageRequest.of(0,size,sort); //查询第0页，共size条数据，按照sort排列
        return typeRepository.findTop(pageable);
    }

    @Override
    public List<Type> listType() {
        return typeRepository.findAll();
    }

    @Transactional
    @Override
    public Type updateType(Long id, Type type) {

        Optional<Type> t = typeRepository.findById(id);
        if(!t.isPresent()){           //判断optional容器是否为空
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(type,t.get()); //t.get()返回的是Type类型。type内容复制到t.get()中，再保存就可以完成更新
        return typeRepository.save(t.get());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        typeRepository.deleteById(id);
    }
}
