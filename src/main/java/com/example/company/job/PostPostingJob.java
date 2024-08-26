package com.example.company.job;

import com.example.company.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostPostingJob {

    private final PostService postService;

    @Scheduled(
            initialDelayString = "${job.post-visibility.initial-delay}",
            fixedDelayString = "${job.post-visibility.fixed-delay}")
    public void run() {
        postService.addToFeedsPostsWithPublicationTimeUp();
    }
}
