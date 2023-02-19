package hexlet.code.Controllers;


import hexlet.code.Models.UrlCheck;
import hexlet.code.Models.Urls;
import hexlet.code.Models.query.QUrls;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UrlController {
    public static Handler createUrl = ctx -> {
        String receivedUrl = ctx.formParam("url");

        if (!isValidURL(receivedUrl)) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        URL url = new URL(receivedUrl);
        String formattedURL = url.getProtocol() + "://" + url.getAuthority();

        if (new QUrls().name.equalTo(formattedURL).exists()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", receivedUrl);
            ctx.redirect("/");
            return;
        }

        Urls newUrl = new Urls(formattedURL);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/");

    };


    static boolean isValidURL(String receivedUrl) throws MalformedURLException {
        try {
            new URL(receivedUrl);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static Handler listUrls = ctx -> {

//        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

        PagedList<Urls> pagedUrls = new QUrls()
//                .name.icontains(term)
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Urls> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;

        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .collect(Collectors.toList());

        ctx.attribute("urls", urls);
//        ctx.attribute("term", term);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urls/index.html");


    };

    public static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
        Urls url = new QUrls()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }
        List<UrlCheck> checks = url.getChecks();

        ctx.attribute("checks", checks);
        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Urls url = new QUrls()
                .id.equalTo(id)
                .findOne();
        try {
            HttpResponse<JsonNode> response = Unirest.get(url.getName()).asJson();

            int statusCode = response.getStatus();


//        String title = response.getStatusText();
//        String h1 = response.ge;
//        String description = ;

            UrlCheck check = new UrlCheck(statusCode, url);
            url.addCheck(check);

            url.save();
            check.save();

            ctx.sessionAttribute("flash", "Проверка выполнена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls/" + id);

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Ошибка");
            ctx.sessionAttribute("flash-type", "alert");
            ctx.redirect("/urls/" + id);
        }

    };
}
