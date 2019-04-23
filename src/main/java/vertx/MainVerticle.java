package vertx;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

import java.util.concurrent.TimeUnit;

public class MainVerticle extends AbstractVerticle {
  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    vertx
        .rxDeployVerticle("vertx.HttpVerticle")
        .subscribe(
            suc -> {
              System.out.println("successfully deployed with id: " + suc);
              startFuture.complete();
            },
            error -> {
              error.printStackTrace();
              startFuture.fail(error);
            }
        );
  }

  public static void main(String[] args) throws InterruptedException {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle("vertx.MainVerticle");
    System.out.println("deployed");

    while (true) {
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Running");
    }
  }
}
