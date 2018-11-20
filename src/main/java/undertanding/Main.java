package undertanding;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

public class Main extends AbstractVerticle {
  @Override
  public void start(final Future<Void> startFuture) {
    vertx.rxDeployVerticle("undertanding.MainV")
        .flatMap(id -> vertx.rxDeployVerticle("undertanding.Message"))
        .subscribe(suc -> System.out.println("Successfully dpeloyed " + suc + " Thread " + Thread.currentThread().getName()));

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle("undertanding.Main");
  }
}
