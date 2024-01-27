package com.javagrunt.service.youtube;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
@Testcontainers
public abstract class AbstractAppTests {

    static Logger logger = LoggerFactory.getLogger(AbstractAppTests.class);

    abstract int getPort();

    private RequestSpecification spec;
    private static final Network network = Network.newNetwork();

    static Network getNetwork() {
        return network;
    }

    @Container
    @ServiceConnection(name = "postgres")
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withExposedPorts(5432)
                    .withNetworkAliases("postgres")
                    .withNetwork(network);

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
    public void shouldReturnIterableListYouTubeVideos() throws Exception {
        Response r = given(this.spec)
                .contentType("application/json")
                .body(youTubeVideoJson())
                .when()
                .port(getPort())
                .post("/api/youTubeVideos")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();

        Response list = given(this.spec)
                .filter(document("index"))
                .when()
                .port(getPort())
                .get("/api/youTubeVideos")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();

        logger.info("Body: " + list.getBody().asString());
    }

    @Test
    public void shouldCreateEntity() throws Exception {
        Response r = given(this.spec)
                .filter(document("create"))
                .contentType("application/json")
                .body(youTubeVideoJson())
                .when()
                .port(getPort())
                .post("/api/youTubeVideos")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();

        logger.info("Body: " + r.getBody().asString());
        assert r.getBody().asString().contains("theid");
    }

    @Test
    public void shouldRetrieveEntity() throws Exception {
        given(this.spec)
                .filter(document("create"))
                .contentType("application/json")
                .body(youTubeVideoJson())
                .when()
                .port(getPort())
                .post("/api/youTubeVideos")
                .then()
                .assertThat().statusCode(is(200));

        Response r2 = given(this.spec)
                .filter(document("get"))
                .when()
                .port(getPort())
                .get("/api/youTubeVideos/theid")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();
        logger.info("Body: " + r2.getBody().asString());
        assert r2.getBody().asString().contains("the title");
    }

    @Test
    public void shouldDeleteEntity() throws Exception {

        Response r = given(this.spec)
                .contentType("application/json")
                .body(youTubeVideoJson())
                .when()
                .port(getPort())
                .post("/api/youTubeVideos")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();

        Response r2 = given(this.spec)
                .filter(document("delete"))
                .when()
                .port(getPort())
                .delete("/api/youTubeVideos/theid")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();
        logger.info("Body: " + r2.getBody().asString());

        given(this.spec)
                .filter(document("deleteFail"))
                .when()
                .port(getPort())
                .delete("/api/youTubeVideos/theid")
                .then()
                .assertThat().statusCode(is(200))
                .extract().response();
    }

    private String youTubeVideoJson() {
        return """
                {
                    "id": "theid",
                    "link": "https://some.link",
                    "description": "the description",
                    "title": "the title",
                    "thumbnail": "the thumbnail",
                    "date": "the date"
                }
                """;
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }
}