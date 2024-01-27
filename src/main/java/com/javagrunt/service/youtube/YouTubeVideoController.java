package com.javagrunt.service.youtube;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/youTubeVideos")
@Slf4j
public class YouTubeVideoController {
    private final YouTubeVideoRepository youTubeVideoRepository;

    public YouTubeVideoController(YouTubeVideoRepository youTubeVideoRepository) {
        this.youTubeVideoRepository = youTubeVideoRepository;
    }

    @GetMapping
    public Iterable<YouTubeVideo> findAll() {
        return youTubeVideoRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public YouTubeVideo findById(@PathVariable String id) {
        return youTubeVideoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("YouTubeVideo not found"));
    }
    
    @PostMapping
    public String create(@RequestBody YouTubeVideo youTubeVideo) {
        log.info("Saving YouTubeVideo: {}", youTubeVideo);
        return youTubeVideoRepository.save(youTubeVideo).getId();
    }

    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable String id) {
        log.info("Deleting YouTubeVideo: {}", id);
        youTubeVideoRepository.deleteById(id);
    }
}
