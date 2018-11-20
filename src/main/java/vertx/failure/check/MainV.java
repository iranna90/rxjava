package vertx.failure.check;

import io.vertx.reactivex.core.Vertx;

public class MainV {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx
        .rxDeployVerticle("vertx.failure.check.EventV")
        .flatMap(id -> vertx.rxDeployVerticle("vertx.failure.check.HttpV"))
        .subscribe(s -> System.out.println("Deployed successfully"));

  }
}
