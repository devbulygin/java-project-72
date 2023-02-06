package hexlet.code.Controllers;

import io.javalin.http.Handler;

public class RootController {
    public static Handler welcome = ctx -> {
        try {
            ctx.attribute("status", "Javalin и Thymeleaf работают");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.render("templates/index.html");
    };



}
