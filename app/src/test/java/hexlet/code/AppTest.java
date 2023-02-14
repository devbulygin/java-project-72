package hexlet.code;

import hexlet.code.Models.Urls;
import hexlet.code.Models.query.QUrls;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
class AppTest {
    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Urls existingUrl;
    private static Database database;


    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        database.script().run("/truncate.sql");
        database.script().run("/seed-test-db.sql");
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
            String url = "https://ru.hexlet.io/teams";
            String correctUrl = "https://ru.hexlet.io";

            HttpResponse<String> responseURL= Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            assertThat(responseURL.getStatus()).isEqualTo(302);
            assertThat(responseURL.getHeaders().getFirst("Location")).isEqualTo("/");

            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("Страница успешно добавлена");

            Urls actualUrl = new QUrls()
                    .name.equalTo(correctUrl)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(correctUrl);

        }

        @Test
        void testCreateDuble() {
            String url = "https://github.com";
            String correctUrl = "https://github.com";

            HttpResponse<String> responseURL= Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            assertThat(responseURL.getStatus()).isEqualTo(302);
            assertThat(responseURL.getHeaders().getFirst("Location")).isEqualTo("/");

            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            String body = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("Страница уже существует");


            Urls actualUrl = new QUrls()
                    .name.equalTo(correctUrl)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(correctUrl);
        }

        @Test
        void testCreateWithError() {
            String url = "site";

            HttpResponse<String> responseURL= Unirest
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

}