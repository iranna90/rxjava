package undertanding;

import java.util.concurrent.TimeUnit;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

public class MainV extends AbstractVerticle {

  @Override
  public void start(final Future<Void> startFuture) {
    Router router = Router.router(vertx);

    router.get("/hello").handler(rc -> {
      vertx.eventBus().rxSend("sent", "from http")
          .subscribe(resply -> {
            System.out.println("Reply was " + resply.body() + " Thread " + Thread.currentThread().getName());
            rc.response().end(resply.body().toString());
          });
    });

    vertx.eventBus().consumer("consume").toObservable()
        .subscribe(message -> {
          System.out.println("consumed is " + message.body() + " Thread " + Thread.currentThread().getName());
          TimeUnit.MILLISECONDS.sleep(500);
          System.out.println("Done consuming after 300 milliseconds");
        });

    vertx.createHttpServer().requestHandler(router::accept).listen(4321);
    System.out.println("deployed http" + " Thread " + Thread.currentThread().getName());
    startFuture.complete();
  }
}
