package com.example.company.service.impl;

import com.example.company.entity.PostEntity;
import com.example.company.enums.AuthorType;
import com.example.company.model.Post;
import com.example.company.repository.BrandRepository;
import com.example.company.repository.FeedRepository;
import com.example.company.repository.PostRepository;
import com.example.company.repository.UserRepository;
import com.example.company.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final PostRepository postRepository;
    private final FeedRepository feedRepository;

    @Override
    @Transactional
    public void save(Post post) {
        switch (post.getAuthorType()) {
            case USER -> {
                var userEntity = userRepository.findByIdOrThrowNotFound(post.getAuthorId());
                var toSavePostEntity = PostEntity.builder()
                        .authorType(AuthorType.USER)
                        .userAuthor(userEntity)
                        .textContent(post.getTextContent())
                        .build();
                var savedPostEntity = postRepository.save(toSavePostEntity);
                var feedEntitiesToSave = userEntity.getFollowers().stream()
                        .map(it -> {
                            var feedEntity = it.getFeed();
                            feedEntity.getPosts().add(savedPostEntity);
                            return feedEntity;
                        })
                        .toList();
                feedRepository.saveAll(feedEntitiesToSave);
            }
            case BRAND -> {
                var brandEntity = brandRepository.findByIdOrThrowNotFound(post.getAuthorId());
                var toSavePostEntity = PostEntity.builder()
                        .authorType(AuthorType.BRAND)
                        .brandAuthor(brandEntity)
                        .textContent(post.getTextContent())
                        .build();
                var savedPostEntity = postRepository.save(toSavePostEntity);
                var feedEntitiesToSave = brandEntity.getFollowers().stream()
                        .map(it -> {
                            var feedEntity = it.getFeed();
                            feedEntity.getPosts().add(savedPostEntity);
                            return feedEntity;
                        })
                        .toList();
                feedRepository.saveAll(feedEntitiesToSave);
            }
        }
    }
}
