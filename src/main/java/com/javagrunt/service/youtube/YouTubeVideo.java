package com.javagrunt.service.youtube;

import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.Id;


@Data
@Entity
public class YouTubeVideo {
    private @Id String id;
    private String link;
    private String description;
    private String title;
    private String thumbnail;
    private String date;
}
