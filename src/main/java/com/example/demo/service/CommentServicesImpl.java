package com.example.demo.service;

import com.example.demo.dao.CommentRepository;
import com.example.demo.po.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServicesImpl implements CommentServices {

    @Autowired
    private CommentRepository commentRepository;

    @Transactional   //放到事务中
    @Override
    public Comment saveComment(Comment comment) {  //保存
        Long parentCommentId=comment.getparentComment().getId();
        if(parentCommentId != -1){
            comment.setparentComment(commentRepository.findById(parentCommentId).get());
        }else{
            comment.setparentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }

    //评论层次逻辑：findByBlogIdAndParentCommentNull查询出所有顶级节点。
    //eachComment方法复制所有顶级节点到另一个Commentlist以防对数据库修改。
    //recursively方法将某个comment和其replycomment合并到一个listcomment中
    //combineChildren将所有回复及更深层回复合并成一个listcomment并set为replycomment（不会对数据库进行修改，因为这个comment是eachComment拷贝过来的）



    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = Sort.by(Sort.Direction.ASC,"createTime");
        List<Comment> comments =  commentRepository.findByBlogIdAndParentCommentNull(blogId,sort);
        return eachComment(comments);
    }

    /**
     * 循环每个顶级的评论节点
     * @param comments
     * @return
     */
    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        //合并评论的各层子代到第一级子代集合中
        combineChildren(commentsView);
        return commentsView;
    }

    /**
     *
     * @param comments root根节点，blog不为空的对象集合
     * @return
     */
    private void combineChildren(List<Comment> comments) {

        for (Comment comment : comments) {
            List<Comment> replys1 = comment.getReplyComment();
            for(Comment reply1 : replys1) {
                //循环迭代，找出子代，存放在tempReplys中
                recursively(reply1);
            }
            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplyComment(tempReplys);
            //清除临时存放区
            tempReplys = new ArrayList<>();
        }
    }

    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplys = new ArrayList<>();
    /**
     * 递归迭代，剥洋葱
     * @param comment 被迭代的对象
     * @return
     */
    private void recursively(Comment comment) {
        tempReplys.add(comment);//顶节点添加到临时存放集合
        if (comment.getReplyComment().size()>0) {
            List<Comment> replys = comment.getReplyComment();
            for (Comment reply : replys) {
                tempReplys.add(reply);
                if (reply.getReplyComment().size()>0) {
                    recursively(reply);
                }
            }
        }
    }
}
