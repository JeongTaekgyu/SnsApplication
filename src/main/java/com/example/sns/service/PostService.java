package com.example.sns.service;

import com.example.sns.model.Comment;
import com.example.sns.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    void create(String title, String body, String userName);
    Post modify(String title, String body, String userName, Integer postId);
    void delete(String userName, Integer postId);
    Page<Post> list(Pageable pageable);
    Page<Post> my(String userName, Pageable pageable);
    void like(Integer postId, String userName);
    long likeCount(Integer postId);
    void comment(Integer postId, String userName, String comment);
    Page<Comment> getComments(Integer postId, Pageable pageable);

}
