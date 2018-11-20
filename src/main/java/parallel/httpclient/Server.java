package parallel.httpclient;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class Server extends AbstractVerticle {


  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(Server.class.getName());
  }

  @Override
  public void start(final Future<Void> startFuture) {
    HttpServer httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.get("/v1").handler(this::handleRequest);
    router.get("/v2").handler(this::handleRequest);
    router.get("/v3").handler(this::handleRequest);
    router.get("/v4").handler(this::handleRequest);
    router.get("/v5").handler(this::handleRequest);

    httpServer.requestHandler(router::accept).listen(1234);
    System.out.println("Deployed successfully");
  }

  private void handleRequest(final RoutingContext rc) {
    System.out.println("Handling request");
    Observable.just(1)
        .doOnNext(
            it -> System.out.println("Thread before delaying " + Thread.currentThread().getName() + " time " + LocalTime.now()))
        .delay(5, TimeUnit.SECONDS)
        .doOnNext(
            it -> System.out.println("Thread after delaying " + Thread.currentThread().getName() + " time " + LocalTime.now()))
        .subscribe(any -> System.out.println("Replying response at " + LocalTime.now() + "Thread " + Thread.currentThread().getName()),
            Throwable::printStackTrace,
            () -> rc.response().end());

  }
}
