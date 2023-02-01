package hexlet.code;

import hexlet.code.Controllers.RootController;
import io.javalin.Javalin;



public class App {

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    private static int getPort() {
        return 8080;
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create();
        addRoutes(app);
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });
        return app;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome);
    }
}
