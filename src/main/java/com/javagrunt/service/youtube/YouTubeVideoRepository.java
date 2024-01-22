package com.javagrunt.service.youtube;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface YouTubeVideoRepository extends PagingAndSortingRepository<YouTubeVideo, String>, CrudRepository<YouTubeVideo, String> {
}
