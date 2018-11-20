package vertx.failure.check;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;

public class HttpV extends AbstractVerticle {

  @Override
  public void start(final Future<Void> startFuture) {

    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.post("/hello")
        .handler(req -> {
          HttpServerRequest request = req.request();
          String header = request.getHeader("content-type");

          if (header.contains("json")) {
            request.toObservable()
                .map(Buffer::toJsonObject)
                .flatMap(json -> vertx.eventBus().<JsonObject>rxSend("check", json).toObservable())
                .subscribe(
                    message -> req.response().end(message.body().encodePrettily()),
                    error -> req.response().end(error.getMessage())
                );
          } else {
            request.toObservable()
                .map(Buffer::toString)
                .flatMap(json -> vertx.eventBus().<JsonObject>rxSend("check", json).toObservable())
                .subscribe(
                    message -> req.response().end(message.body().encodePrettily()),
                    error -> {
                      System.out.println("Error occurred while sending message " + error.getMessage());
                      req.response().end("error " + error.getMessage());
                    }
                );
          }
        });

    httpServer.requestHandler(router::accept).listen(1234);
    startFuture.complete();
  }
}
