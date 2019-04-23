package vertx;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.Future;
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class Check extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(Check.class.getName());
  }

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/hello").handler(this::handlerReq);
    httpServer.requestHandler(router::accept).listen(1234);
    startFuture.complete();
  }

  private void handlerReq(final RoutingContext rc) {
    Observable.just(1, 2, 3, 4)
        .doOnNext(it -> System.out.println("item is: " + it + " Thread is " + Thread.currentThread().getName()))
        .observeOn(Schedulers.io())
        .doOnNext(it -> System.out.println("item is: " + it + " after first Thread is " + Thread.currentThread().getName()))
        .observeOn(RxHelper.scheduler(vertx.getDelegate()))
        .doOnNext(it -> System.out.println("item is: " + it + " after second Thread is " + Thread.currentThread().getName()))
        .subscribe(
            it -> System.out.println("item is: " + it + " sub Thread is " + Thread.currentThread().getName())
        );

    rc.response().end("hello");

  }
}
