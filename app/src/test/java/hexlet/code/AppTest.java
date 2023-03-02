package hexlet.code;


import hexlet.code.Models.Url;
import hexlet.code.Models.query.QUrl;
import io.ebean.DB;
import io.ebean.SqlRow;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {
    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static MockWebServer server;
    private static String testUrl;

    private static Transaction transaction;



    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setBody(readFixture("index.html"));
        server.enqueue(mockedResponse);
        server.start();

    }


    @BeforeAll
    static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        testUrl = "https://github.com";
    }

    @AfterAll
    static void afterAll() throws IOException {

        app.stop();
        server.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
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
        void testIndex() {

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("http://github.com");
            assertThat(body).contains("200");
        }

        @Test
        void testShow() {

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls" + "/1")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("http://github.com");
            assertThat(body).contains("200");
            assertThat(body).contains("best platform");

        }


        @Test
        void testCreateDouble() {

            HttpResponse<String> responseURL = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", testUrl)
                    .asEmpty();

            assertThat(responseURL.getStatus()).isEqualTo(302);
            assertThat(responseURL.getHeaders().getFirst("Location")).isEqualTo("/urls");

            Url actualUrl = new QUrl()
                    .name.equalTo(testUrl)
                    .setMaxRows(1)
                    .findList()
                    .stream()
                    .findFirst()
                    .orElse(null);

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(testUrl);
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

            assertThat(body).contains("https://github.com");
        }
    }

    @Nested
    class UrlCheckTest {

        @Test
        void testStore() {
            String url = server.url("/").toString().replaceAll("/$", "");

            Unirest.post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            String selectUrl = String.format("SELECT * FROM url WHERE name = '%s';", url);
            SqlRow actualUrl = DB.sqlQuery(selectUrl).findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getString("name")).isEqualTo(url);

            Unirest.post(baseUrl + "/urls/" + actualUrl.getString("id") + "/checks")
                    .asEmpty();

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/" + actualUrl.getString("id"))
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("Страница успешно проверена");

            String selectUrlCheck = String.format(
                    "SELECT * FROM url_check WHERE url_id = '%s' ORDER BY created_at DESC;",
                    actualUrl.getString("id")
            );
            SqlRow actualCheckUrl = DB.sqlQuery(selectUrlCheck).findOne();

            assertThat(actualCheckUrl).isNotNull();
            assertThat(actualCheckUrl.getString("status_code")).isEqualTo("200");
            assertThat(actualCheckUrl.getString("title")).isEqualTo("Title test");
            assertThat(actualCheckUrl.getString("h1")).isEqualTo("h1 Test");
            assertThat(actualCheckUrl.getString("description")).contains("description test");
        }
    }

}

