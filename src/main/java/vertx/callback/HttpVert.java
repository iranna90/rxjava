package vertx.callback;

import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.impl.FailedFuture;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

import static java.lang.String.format;

public class HttpVert extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router
        .get("/details")
        .handler(this::handle);

    httpServer.requestHandler(router::accept).listen(1234);

    System.out.println("EventV verticle deployed successfully in Thread " + Thread.currentThread().getName());
  }

  private void handle(final RoutingContext rc) {
    vertx.eventBus().<JsonObject>send("check", new JsonObject().put("http", "from EventV"), rep -> {
      if (rep.failed()) {
        ReplyException exp = (ReplyException) rep.cause();
        System.out.println("Error in message verticle");
        System.out.println(exp.failureCode());
        rc.response().setStatusCode(500).end(rep.cause().getMessage());
        return;
      }
      String s = rep.result().body().encodePrettily();
      System.out.println(format("Handling reply in Thread: [%s] and returning response; [%s]", Thread.currentThread().getName(),
          s));
      rc.response().end(s);


    });
  }
}
