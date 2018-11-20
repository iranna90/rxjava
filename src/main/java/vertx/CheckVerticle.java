package vertx;

import java.util.concurrent.TimeUnit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CheckVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {

    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/check")
        .handler(this::handler);

    httpServer.requestHandler(router::accept).listen(1234);
    System.out.println("deployed successfully");
  }

  private void handler(final RoutingContext rc) {
    try {
      TimeUnit.MILLISECONDS.sleep(500);
      System.out.println("Slept for 500");
      TimeUnit.MILLISECONDS.sleep(500);
      System.out.println("Slept for another 500");
      TimeUnit.MILLISECONDS.sleep(500);
      System.out.println("Slept for another 500 third time");
      rc.response().end("success");
      System.out.println("responded successfully");

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
