package com.javagrunt.service.youtube;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
@Testcontainers
public abstract class AbstractAppTests {
    
    private YouTubeVideoRepository youTubeVideoRepository;

    static Logger logger = LoggerFactory.getLogger(AbstractAppTests.class);

    abstract int getPort();

    private RequestSpecification spec;
    private static final Network network = Network.newNetwork();

    static Network getNetwork() {
        return network;
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withExposedPorts(5432)
                .withNetworkAliases("postgres")
                .withNetwork(network);
    }

//    @BeforeEach
//    public void deleteAllBeforeTests() throws Exception {
//        youTubeVideoRepository.deleteAll();
//    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(
                                        modifyUris()
                                        .scheme("https")
                                        .host("javagrunt.com")
                                        .removePort()))
                        .build();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {
        given(this.spec)
                .filter(document("index", links(halLinks(),
                        linkWithRel("youTubeVideos").description("YouTube videos"))))
                .when()
                .port(getPort())
                .get("/")
                .then()
                .assertThat().statusCode(is(200));
    }

//    @Test
//    public void shouldCreateEntity() throws Exception {
//
//        mockMvc.perform(post("/youTubeVideo").content(
//                "{\"id\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isCreated()).andExpect(
//                header().string("Location", containsString("people/")));
//    }
//
//    @Test
//    public void shouldRetrieveEntity() throws Exception {
//
//        MvcResult mvcResult = mockMvc.perform(post("/people").content(
//                "{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isCreated()).andReturn();
//
//        String location = mvcResult.getResponse().getHeader("Location");
//        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
//                jsonPath("$.firstName").value("Frodo")).andExpect(
//                jsonPath("$.lastName").value("Baggins"));
//    }
//
//    @Test
//    public void shouldQueryEntity() throws Exception {
//
//        mockMvc.perform(post("/people").content(
//                "{ \"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isCreated());
//
//        mockMvc.perform(
//                get("/people/search/findByLastName?name={name}", "Baggins")).andExpect(
//                status().isOk()).andExpect(
//                jsonPath("$._embedded.people[0].firstName").value(
//                        "Frodo"));
//    }
//
//    @Test
//    public void shouldUpdateEntity() throws Exception {
//
//        MvcResult mvcResult = mockMvc.perform(post("/people").content(
//                "{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isCreated()).andReturn();
//
//        String location = mvcResult.getResponse().getHeader("Location");
//
//        mockMvc.perform(put(location).content(
//                "{\"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isNoContent());
//
//        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
//                jsonPath("$.firstName").value("Bilbo")).andExpect(
//                jsonPath("$.lastName").value("Baggins"));
//    }
//
//    @Test
//    public void shouldPartiallyUpdateEntity() throws Exception {
//
//        MvcResult mvcResult = mockMvc.perform(post("/people").content(
//                "{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isCreated()).andReturn();
//
//        String location = mvcResult.getResponse().getHeader("Location");
//
//        mockMvc.perform(
//                patch(location).content("{\"firstName\": \"Bilbo Jr.\"}")).andExpect(
//                status().isNoContent());
//
//        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
//                jsonPath("$.firstName").value("Bilbo Jr.")).andExpect(
//                jsonPath("$.lastName").value("Baggins"));
//    }
//
//    @Test
//    public void shouldDeleteEntity() throws Exception {
//
//        MvcResult mvcResult = mockMvc.perform(post("/people").content(
//                "{ \"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
//                status().isCreated()).andReturn();
//
//        String location = mvcResult.getResponse().getHeader("Location");
//        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
//
//        mockMvc.perform(get(location)).andExpect(status().isNotFound());
//    }
}