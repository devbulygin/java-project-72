package hexlet.code.Controllers;


import hexlet.code.Models.Urls;
import hexlet.code.Models.query.QUrls;
import io.javalin.http.Handler;

import java.net.URL;

public class UrlController {
    public static Handler createUrl = ctx -> {
        String receivedUrl = ctx.formParam("url");
        URL url = new URL(receivedUrl);
        if (url.getProtocol().isEmpty() && url.getAuthority().isEmpty()) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", receivedUrl);
            ctx.render("articles/new.html");
            return;
        }
        String formattedURL = url.getProtocol() + "://" + url.getAuthority();

        if (new QUrls().name.equalTo(formattedURL).exists()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", receivedUrl);
            ctx.render("articles/new.html");
            return;
        }
       Urls newUrl = new Urls(formattedURL);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/articles");

    };


//    public static Handler listUrls = ctx -> {
//
//    }
//
//    public static Handler showUrl = ctx -> {
//
//    }
}
