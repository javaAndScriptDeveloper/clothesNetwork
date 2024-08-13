package com.example.company.service;

import com.example.company.model.post.Post;
import java.util.List;

public interface FeedService {

    List<Post> searchFeed(Long userId, Integer startPosition, Integer step);
}
