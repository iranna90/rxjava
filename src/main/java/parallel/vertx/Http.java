package parallel.vertx;

import java.time.LocalTime;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class Http extends AbstractVerticle {
  @Override
  public void start(final Future<Void> startFuture) {
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);

    router.post("/check").handler(this::handle);
    router.get().handler(this::getHandler);

    httpServer.requestHandler(router::accept).listen(4321);
    startFuture.complete();
  }

  private void getHandler(final RoutingContext rc) {
    System.out.println("get call message: on thread " + Thread.currentThread().getName() + " at " + LocalTime.now());
    rc.response().end("hello");
  }

  private void handle(final RoutingContext rc) {
    rc.request().toObservable()
        .map(buffer -> buffer.toString("UTF-8"))
        .flatMap(str -> vertx.eventBus().<String>rxSend("listen", str).map(Message::body).toObservable())
        .subscribe(
            item -> {
              rc.response().end(item);
            },
            error -> rc.response().end(error.getMessage()),
            () -> System.out.println("completed http request"),
            disposable -> System.out.println("acting on http request")
        );
  }

}
