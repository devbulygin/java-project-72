package hexlet.code.Controllers;


import hexlet.code.Models.Url;
import hexlet.code.Models.UrlCheck;
import hexlet.code.Models.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

        if (new QUrl().name.equalTo(formattedURL).exists()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", receivedUrl);
            ctx.redirect("/");
            return;
        }

        Url newUrl = new Url(formattedURL);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");

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

        PagedList<Url> pagedUrls = new QUrl()
//                .name.icontains(term)
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

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
        Url url = new QUrl()
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
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        String title = new String();
        String h1 = new String();
        String description = new String();


        try {
            System.out.println("url to parse: " + url.getName());
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document doc = Jsoup.parse(response.getBody());

            int statusCode = response.getStatus();

            Document document = Jsoup.connect(url.getName()).timeout(10 * 1000).get();
            Element titleElement = doc.selectFirst("title");
            Element h1Element = doc.selectFirst("h1");
            Element metaElement = doc.selectFirst("meta[name=description][content]");
            if (titleElement != null) {
                title = doc.title();
            } else {
                title = "Тег title не найден";
            }

            if (h1Element != null) {
                h1 = doc.selectFirst("h1").text();
            } else {
                h1 = "Тег h1 не найден";
            }

            if (metaElement != null) {
                description = doc.selectFirst("meta[name=description][content]").text();
            } else {
                description = "Тег  <meta name=\"description\" content=\"...\">  не найден";
            }

            UrlCheck check = new UrlCheck(statusCode, title, h1, description, url);

            url.addCheck(check);

            url.save();
            check.save();

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.render("/urls/" + id);

        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "alert");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "alert");
        }

    };
}
