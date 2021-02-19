package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.dao.TagRepository;
import com.example.demo.po.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TagServiceImpl implements TagService{

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public Optional<Tag> getTag(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> listIndexTag(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size"); //拥有该tag的blog的数量,按照这个排倒序
        Pageable pageable = PageRequest.of(0,size,sort);
        return tagRepository.findTop(pageable);
    }

    @Override
    public List<Tag> listTag(String ids) {   //ids=1,2,3....
        return tagRepository.findAllById(convertToList(ids));
    }
    private List<Long> convertToList(String ids){   //将前台传过来id组成的字符串做成一个list
        List<Long> list = new ArrayList<>();
        if (!"".equals(ids) && ids != null){
            String[] idarray = ids.split(",");
            for(int i=0;i<idarray.length;i++){
                list.add(new Long(idarray[i]));
            }
        }
        return list;
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {
        Optional<Tag> t = tagRepository.findById(id);
        if(!t.isPresent()){           //判断optional容器是否为空
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(tag,t.get()); //t.get()返回的是Type类型。type内容复制到t.get()中，再保存就可以完成更新
        return tagRepository.save(t.get());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}
