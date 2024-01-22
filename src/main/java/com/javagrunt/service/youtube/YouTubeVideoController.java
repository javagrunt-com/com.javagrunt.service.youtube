package com.javagrunt.service.youtube;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/youTubeVideos")
public class YouTubeVideoController {
    private final YouTubeVideoRepository youTubeVideoRepository;

    public YouTubeVideoController(YouTubeVideoRepository youTubeVideoRepository) {
        this.youTubeVideoRepository = youTubeVideoRepository;
    }

    @GetMapping
    public Iterable<YouTubeVideo> findAll() {
        return youTubeVideoRepository.findAll(Sort.by("date").descending());
    }
}
