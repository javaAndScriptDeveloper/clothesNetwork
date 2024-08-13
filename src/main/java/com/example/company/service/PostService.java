package com.example.company.service;

import com.example.company.model.post.Post;

public interface PostService {

    void create(Post post);

    void update(Post post);

    void addToFeedsVisiblePosts();
}
