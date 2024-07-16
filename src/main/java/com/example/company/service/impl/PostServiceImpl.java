package com.example.company.service.impl;

import com.example.company.entity.BrandEntity;
import com.example.company.entity.FeedEntity;
import com.example.company.entity.PostEntity;
import com.example.company.entity.UserEntity;
import com.example.company.entity.info.ViewConditionInfo;
import com.example.company.enums.AuthorType;
import com.example.company.enums.SearchOperator;
import com.example.company.mapper.model.ViewConditionModelMapper;
import com.example.company.model.Post;
import com.example.company.model.search.SearchRequest;
import com.example.company.repository.BrandRepository;
import com.example.company.repository.FeedRepository;
import com.example.company.repository.PostRepository;
import com.example.company.repository.UserRepository;
import com.example.company.service.PostService;
import com.example.company.service.SearchService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final SearchService searchService;
    private final ViewConditionModelMapper viewConditionModelMapper;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final PostRepository postRepository;
    private final FeedRepository feedRepository;

    @Override
    @Transactional
    public void create(Post post) {
        switch (post.getAuthorType()) {
            case USER -> {
                var userEntity = userRepository.findByIdOrThrowNotFound(post.getAuthorId());
                var toSavePostEntity = PostEntity.builder()
                        .authorType(AuthorType.USER)
                        .userAuthor(userEntity)
                        .textContent(post.getTextContent())
                        .viewConditions(viewConditionModelMapper.toEntity(post.getViewConditions()))
                        .build();
                var savedPostEntity = postRepository.save(toSavePostEntity);
                userEntity.getPosts().add(savedPostEntity);
                userRepository.save(userEntity);
                var feedEntitiesToSave = generateFeedEntitiesToSave(savedPostEntity, userEntity);
                feedRepository.saveAll(feedEntitiesToSave);
            }
            case BRAND -> {
                var brandEntity = brandRepository.findByIdOrThrowNotFound(post.getAuthorId());
                var toSavePostEntity = PostEntity.builder()
                        .authorType(AuthorType.BRAND)
                        .brandAuthor(brandEntity)
                        .textContent(post.getTextContent())
                        .viewConditions(viewConditionModelMapper.toEntity(post.getViewConditions()))
                        .build();
                var savedPostEntity = postRepository.save(toSavePostEntity);
                brandEntity.getPosts().add(savedPostEntity);
                brandRepository.save(brandEntity);
                var feedEntitiesToSave = generateFeedEntitiesToSave(savedPostEntity, brandEntity);
                feedRepository.saveAll(feedEntitiesToSave);
            }
        }
    }

    @Override
    @Transactional
    public void update(Post post) {
        var initialPostEntity = postRepository.findByIdOrThrowNotFound(post.getId());
        initialPostEntity.setTextContent(post.getTextContent());
        var initialViewConditions = initialPostEntity.getViewConditions();
        initialPostEntity.setViewConditions(viewConditionModelMapper.toEntity(post.getViewConditions()));
        var updatedPostEntity = postRepository.save(initialPostEntity);
        updateVisibilityIfChanged(initialViewConditions, updatedPostEntity);
    }

    private void updateVisibilityIfChanged(
            List<ViewConditionInfo> initialViewConditions, PostEntity updatedPostEntity) {
        var updatedViewConditions = updatedPostEntity.getViewConditions();
        if (initialViewConditions.equals(updatedViewConditions)) {
            return;
        }

        updatedPostEntity.getFeeds().clear();
        postRepository.save(updatedPostEntity);

        switch (updatedPostEntity.getAuthorType()) {
            case USER -> {
                var userEntity = updatedPostEntity.getUserAuthor();
                var feedEntitiesToSave = generateFeedEntitiesToSave(updatedPostEntity, userEntity);
                var savedFeedEntities = feedRepository.saveAll(feedEntitiesToSave);
                updatedPostEntity.getFeeds().addAll(savedFeedEntities);
                postRepository.save(updatedPostEntity);
            }
            case BRAND -> {
                var brandEntity = updatedPostEntity.getBrandAuthor();
                var feedEntitiesToSave = generateFeedEntitiesToSave(updatedPostEntity, brandEntity);
                var savedFeedEntities = feedRepository.saveAll(feedEntitiesToSave);
                updatedPostEntity.getFeeds().addAll(savedFeedEntities);
                postRepository.save(updatedPostEntity);
            }
        }
    }

    private List<FeedEntity> generateFeedEntitiesToSave(PostEntity postEntity, UserEntity userEntity) {
        var usersForPost = postEntity.getViewConditions() == null
                ? userEntity.getFollowers()
                : findUsersForPost(postEntity, AuthorType.USER);
        return usersForPost.stream()
                .map(it -> {
                    var feedEntity = it.getFeed();
                    feedEntity.getPosts().add(postEntity);
                    return feedEntity;
                })
                .toList();
    }

    private List<FeedEntity> generateFeedEntitiesToSave(PostEntity postEntity, BrandEntity brandEntity) {
        var usersForPost = postEntity.getViewConditions() == null
                ? brandEntity.getFollowers()
                : findUsersForPost(postEntity, AuthorType.BRAND);
        return usersForPost.stream()
                .map(it -> {
                    var feedEntity = it.getFeed();
                    feedEntity.getPosts().add(postEntity);
                    return feedEntity;
                })
                .toList();
    }

    private List<UserEntity> findUsersForPost(PostEntity postEntity, AuthorType authorType) {
        var filteringFields = new ArrayList<SearchRequest.FilteringField>();

        var subscribedToUserFilteringField = SearchRequest.FilteringField.builder()
                .fieldName(authorType == AuthorType.USER ? "subscribedUsers.id" : "subscribedBrands.id")
                .operator(SearchOperator.EQUALS)
                .fieldValue(
                        authorType == AuthorType.USER
                                ? postEntity.getUserAuthor().getId()
                                : postEntity.getBrandAuthor().getId())
                .build();

        filteringFields.add(subscribedToUserFilteringField);
        var filteringFieldsFromViewConditions = viewConditionModelMapper.toModel(postEntity.getViewConditions());
        filteringFields.addAll(filteringFieldsFromViewConditions);

        var searchRequest = SearchRequest.<UserEntity>builder()
                .clazz(UserEntity.class)
                .filteringFields(filteringFields)
                .build();
        return searchService.search(searchRequest);
    }
}
