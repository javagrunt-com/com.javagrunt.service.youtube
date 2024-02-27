package com.javagrunt.service.youtube;

import io.clue2solve.spring.cloud.starter.youtube.services.YouTubeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/openai")
@Slf4j
public class OpenAiController {

    private String promptTemplate;

    private final ChatClient chatClient;

    private final ImageClient openaiImageClient;

    private YouTubeService youTubeService;

    public OpenAiController(ChatClient chatClient,
                            ImageClient openaiImageClient,
                            @Value("${app.promptTemplate}") String promptTemplate,
                            YouTubeService youTubeService) {
        this.chatClient = chatClient;
        this.openaiImageClient = openaiImageClient;
        this.promptTemplate = promptTemplate;
        this.youTubeService = youTubeService;
    }

    @GetMapping("/generate/{id}")
    public void generateStuff(@PathVariable String id) {
        String transcript = downloadCaption(id);
        log.info(transcript);

        BeanOutputParser<YouTubeVideoResponse> parser =
                new BeanOutputParser<>(YouTubeVideoResponse.class);
        String format = parser.getFormat();
        log.info("format {}", format);

        PromptTemplate pt = new PromptTemplate(promptTemplate);
        Prompt renderedPrompt = pt.create(Map.of("transcript", transcript, "format", format));

        ChatResponse response = chatClient.call(renderedPrompt);
        log.info("Result: " + response.getResult());

        Usage usage = response.getMetadata().getUsage();
        log.info(new StringBuilder().append("Usage: ").append(usage.getPromptTokens()).append(" ")
                .append(usage.getGenerationTokens()).append("; ").append(usage.getTotalTokens()).toString());

        var options = ImageOptionsBuilder.builder()
                .withModel("dall-e-3")
                .withWidth(1792)
                .withHeight(1024)
                .build();
        ImagePrompt imagePrompt = getImagePrompt(options);

        ImageResponse imageResponse = openaiImageClient.call(imagePrompt);

        ImageResponseMetadata imageResponseMetadata = imageResponse.getMetadata();
        log.info("ImageResponseMetaData: {}", imageResponseMetadata);

        var generation = imageResponse.getResult();
        Image image = generation.getOutput();
        log.info("Image URL: {}", image.getUrl());

        var imageGenerationMetadata = generation.getMetadata();
        OpenAiImageGenerationMetadata openAiImageGenerationMetadata = (OpenAiImageGenerationMetadata) imageGenerationMetadata;
    }

    private static ImagePrompt getImagePrompt(ImageOptions options) {
        var instructions = """
                Create a vibrant and eye-catching image.
                It should feature a high-contrast color scheme with bright, bold colors.
                In the center, place a large, intriguing, and slightly exaggerated facial expression of a person looking surprised or excited,
                ensuring the emotion is highly visible and engaging. Include an element of mystery or curiosity,
                such as a blurred background image or an object that's partially visible, to spark viewers' interest.
                Ensure the text color contrasts well with the background for readability.
                The overall design should be clean, not too cluttered, but with enough visual elements to stand out and grab attention in a sea of other thumbnails.""";

        ImagePrompt imagePrompt = new ImagePrompt(instructions, options);
        return imagePrompt;
    }

    private String downloadCaption(String id) {
        String youTubeVideoId = id.substring(9);
        log.info("YouTubeVideoId: {}", youTubeVideoId);
        String caption;
        try {
            caption = youTubeService.downloadCaption(youTubeVideoId, "en");
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            return "";
        }
        return caption;
    }

}

record YouTubeVideoResponse(
        String title,
        String summary,
        String[] seoTags,
        String socialMediaPost) {
}