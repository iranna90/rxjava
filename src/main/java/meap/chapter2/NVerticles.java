package meap.chapter2;

import java.util.concurrent.TimeUnit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class NVerticles extends AbstractVerticle {

  private final int number;

  public NVerticles(int number) {
    this.number = number;
  }

  @Override
  public void start(final Future<Void> startFuture) {

    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/").handler(rc -> {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      String result = "Thread " + Thread.currentThread().getName() + " veeticle " + this.number;
      System.out.println(result);
      rc.response().end(result);
    });

    httpServer.requestHandler(router::accept).listen(8080);
  }


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    for (int i = 0; i < 20; i++) {
      vertx.deployVerticle(new NVerticles(i));
    }

    System.out.println("All verticles deployed");
  }
}
