package com.javagrunt.service.youtube;

import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenAiImageClientConfig {
    @Bean
    public OpenAiImageApi openAiImageApi() {
        return new OpenAiImageApi(getApiKey());
    }

    @Bean
    public OpenAiImageClient openAiImageClient(OpenAiImageApi imageApi) {
        OpenAiImageClient openAiImageClient = new OpenAiImageClient(imageApi);
        // openAiImageClient.setModel("foobar");
        return openAiImageClient;
    }

    private String getApiKey() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalArgumentException(
                    "You must provide an API key.  Put it in an environment variable under the name OPENAI_API_KEY");
        }
        return apiKey;
    }
}
