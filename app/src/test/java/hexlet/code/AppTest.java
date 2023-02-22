package hexlet.code;


import hexlet.code.Models.Url;
import hexlet.code.Models.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {
//    @Test
//    void testInit() {
//        assertThat(true).isEqualTo(true);
//    }

    private static Javalin app;
    private static String baseUrl;
    private static Url existingUrl;
    private static Database database;
    private static MockWebServer server;
    private static MockResponse response;
    private static String url;


    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        response = new MockResponse()
                .setBody("<meta charset=\"UTF-8\" name=\"description\" content=\"description test\"> "
                        + "<title>Title test</title> "
                        + "<h1>h1 Test</h1>");

        server.enqueue(response);

        server.start();
        url = "http://example.test";
        HttpUrl httpUrl = server.url(url);

    }


    @BeforeAll
    static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @AfterAll
    static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        try {
            database.script().run("/truncate.sql");
        } catch (PersistenceException ex) {
            ex.printStackTrace();
        }
        database.script().run("/seed-test-db.sql");
    }

    @AfterEach
    void afterEach() throws IOException {

        server.shutdown();
    }


    @Nested
    class RootTest {
        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Анализатор страниц");
        }
    }

    @Nested
    class UrlTest {
        @Test
        void testCreateNew() {


            HttpResponse<String> responseURL = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            assertThat(responseURL.getStatus()).isEqualTo(302);
            assertThat(responseURL.getHeaders().getFirst("Location")).isEqualTo("/");

            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
//            assertThat(body).contains("Страница успешно добавлена");

            Url actualUrl = new QUrl()
                    .name.equalTo(url)
                    .setMaxRows(1)
                    .findList()
                    .stream()
                    .findFirst()
                    .orElse(null);

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(url);

        }

        @Test
        void testCreateDuble() {


            HttpResponse<String> responseURL = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url.toString())
                    .asEmpty();

            assertThat(responseURL.getStatus()).isEqualTo(302);
            assertThat(responseURL.getHeaders().getFirst("Location")).isEqualTo("/");

            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            String body = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("Страница уже существует");


            Url actualUrl = new QUrl()
                    .name.equalTo(url)
                    .setMaxRows(1)
                    .findList()
                    .stream()
                    .findFirst()
                    .orElse(null);

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(url);
        }

        @Test
        void testCreateWithError() {
            String url = "site";

            HttpResponse<String> responseURL = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            assertThat(responseURL.getStatus()).isEqualTo(302);
            assertThat(responseURL.getHeaders().getFirst("Location")).isEqualTo("/");

            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            String body = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("Некорректный URL");
        }

        @Test
        void testListUrl() {


            HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();

            String body = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);

            assertThat(body).contains(url);
        }
    }

}

