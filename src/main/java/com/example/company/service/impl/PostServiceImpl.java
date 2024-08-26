package com.example.company.service.impl;

import com.example.company.entity.BrandEntity;
import com.example.company.entity.FeedEntity;
import com.example.company.entity.PostEntity;
import com.example.company.entity.UserEntity;
import com.example.company.entity.info.ViewConditionInfo;
import com.example.company.enums.AuthorType;
import com.example.company.enums.SearchOperator;
import com.example.company.mapper.model.ViewConditionModelMapper;
import com.example.company.model.post.Post;
import com.example.company.model.search.SearchRequest;
import com.example.company.repository.BrandRepository;
import com.example.company.repository.FeedRepository;
import com.example.company.repository.PostRepository;
import com.example.company.repository.UserRepository;
import com.example.company.service.PostService;
import com.example.company.service.SearchService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
                        .publicationTime(
                                Instant.ofEpochMilli(post.getPublicationTime().getValue()))
                        .visible(post.getVisible())
                        .posted(post.getPublicationTime() == null)
                        .build();
                var savedPostEntity = postRepository.save(toSavePostEntity);
                userEntity.getPosts().add(savedPostEntity);
                userRepository.save(userEntity);
                if (savedPostEntity.getVisible()) {
                    var feedEntitiesToSave = resolveFeedEntitiesToSave(savedPostEntity, userEntity);
                    feedRepository.saveAll(feedEntitiesToSave);
                    log.info(
                            "Saved post, affected {} feeds; postId: {}, authorId: {}, authorType: {}",
                            feedEntitiesToSave.size(),
                            savedPostEntity.getId(),
                            savedPostEntity.getUserAuthor().getId(),
                            AuthorType.USER);
                } else {
                    log.info(
                            "Saved post, not visible; postId: {}, authorId: {}, authorType: {}",
                            savedPostEntity.getId(),
                            savedPostEntity.getUserAuthor().getId(),
                            AuthorType.USER);
                }
            }
            case BRAND -> {
                var brandEntity = brandRepository.findByIdOrThrowNotFound(post.getAuthorId());
                var toSavePostEntity = PostEntity.builder()
                        .authorType(AuthorType.BRAND)
                        .brandAuthor(brandEntity)
                        .textContent(post.getTextContent())
                        .viewConditions(viewConditionModelMapper.toEntity(post.getViewConditions()))
                        .publicationTime(
                                Instant.ofEpochMilli(post.getPublicationTime().getValue()))
                        .visible(post.getVisible())
                        .posted(post.getPublicationTime() == null)
                        .build();
                var savedPostEntity = postRepository.save(toSavePostEntity);
                brandEntity.getPosts().add(savedPostEntity);
                brandRepository.save(brandEntity);
                if (savedPostEntity.getVisible()) {
                    var feedEntitiesToSave = resolveFeedEntitiesToSave(savedPostEntity, brandEntity);
                    feedRepository.saveAll(feedEntitiesToSave);
                    log.info(
                            "Saved post, affected {} feeds; postId: {}, authorId: {}, authorType: {}",
                            feedEntitiesToSave.size(),
                            savedPostEntity.getId(),
                            savedPostEntity.getBrandAuthor().getId(),
                            AuthorType.BRAND);
                } else {
                    log.info(
                            "Saved post, not visible; postId: {}, authorId: {}, authorType: {}",
                            savedPostEntity.getId(),
                            savedPostEntity.getBrandAuthor().getId(),
                            AuthorType.BRAND);
                }
            }
        }
    }

    @Override
    @Transactional
    public void update(Post post) {
        log.info(
                "Starting post update; postId: {}, authorId: {}, authorType: {}",
                post.getId(),
                post.getAuthorId(),
                post.getAuthorType());
        var initialPostEntity = postRepository.findByIdOrThrowNotFound(post.getId());
        initialPostEntity.setTextContent(post.getTextContent());
        initialPostEntity.setVisible(post.getVisible());
        initialPostEntity.setPublicationTime(
                Instant.ofEpochMilli(post.getPublicationTime().getValue()));
        var initialViewConditions = initialPostEntity.getViewConditions();
        initialPostEntity.setViewConditions(viewConditionModelMapper.toEntity(post.getViewConditions()));
        if (visibilityChangedToInvisible(initialPostEntity, post)) {
            initialPostEntity.getFeeds().clear();
        }
        var updatedPostEntity = postRepository.save(initialPostEntity);
        if (updatedPostEntity.getVisible()) {
            updateFeedsOnViewConditionsChanged(initialViewConditions, updatedPostEntity);
        }
        log.info("Updated post; postId: {}", updatedPostEntity.getId());
    }

    private boolean visibilityChangedToInvisible(PostEntity initialPostEntity, Post toUpdatePostModel) {
        return !initialPostEntity.getVisible() && toUpdatePostModel.getVisible();
    }

    private void updateFeedsOnViewConditionsChanged(
            List<ViewConditionInfo> initialViewConditions, PostEntity updatedPostEntity) {
        var updatedViewConditions = updatedPostEntity.getViewConditions();
        if (initialViewConditions.equals(updatedViewConditions)) {
            log.info(
                    "Skipped updating feeds: post view conditions has not changed; postId: {}",
                    updatedPostEntity.getId());
            return;
        }

        updatedPostEntity.getFeeds().clear();
        postRepository.save(updatedPostEntity);

        switch (updatedPostEntity.getAuthorType()) {
            case USER -> {
                var userEntity = updatedPostEntity.getUserAuthor();
                var feedEntitiesToSave = resolveFeedEntitiesToSave(updatedPostEntity, userEntity);
                var savedFeedEntities = feedRepository.saveAll(feedEntitiesToSave);
                updatedPostEntity.getFeeds().addAll(savedFeedEntities);
                postRepository.save(updatedPostEntity);
                log.info(
                        "Updated feeds for post, new feeds size: {}; postId: {}",
                        savedFeedEntities.size(),
                        updatedPostEntity.getId());
            }
            case BRAND -> {
                var brandEntity = updatedPostEntity.getBrandAuthor();
                var feedEntitiesToSave = resolveFeedEntitiesToSave(updatedPostEntity, brandEntity);
                var savedFeedEntities = feedRepository.saveAll(feedEntitiesToSave);
                updatedPostEntity.getFeeds().addAll(savedFeedEntities);
                postRepository.save(updatedPostEntity);
                log.info(
                        "Updated feeds for post, new feeds size: {}; postId: {}",
                        savedFeedEntities.size(),
                        updatedPostEntity.getId());
            }
        }
    }

    private List<FeedEntity> resolveFeedEntitiesToSave(PostEntity postEntity, UserEntity userEntity) {
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

    private List<FeedEntity> resolveFeedEntitiesToSave(PostEntity postEntity, BrandEntity brandEntity) {
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

    @Override
    @Transactional
    public void addToFeedsPostsWithPublicationTimeUp() {
        var postEntitiesToAdd = postRepository.findAllByPublicationTimeIsBeforeAndPostedIsFalse(Instant.now());
        postEntitiesToAdd.forEach(postEntity -> {
            postEntity.setVisible(true);
            postEntity.setPosted(true);
            postEntity.setPublicationTime(null);
        });
        var feedEntitiesToUpdate = postEntitiesToAdd.stream()
                .map(postEntity -> switch (postEntity.getAuthorType()) {
                    case USER -> resolveFeedEntitiesToSave(postEntity, postEntity.getUserAuthor());
                    case BRAND -> resolveFeedEntitiesToSave(postEntity, postEntity.getBrandAuthor());
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        var updatedPosts = postRepository.saveAll(postEntitiesToAdd);
        var updatedFeeds = feedRepository.saveAll(feedEntitiesToUpdate);

        log.info("Posted {} posts, affected {} feeds", updatedPosts.size(), updatedFeeds.size());
    }
}
