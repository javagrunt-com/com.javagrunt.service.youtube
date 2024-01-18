package com.javagrunt.service.youtube;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppTests extends AbstractAppTests {
    
    @LocalServerPort
    private int port;
    
    @Override
    int getPort() {
        return this.port;
    }
    
    
}
