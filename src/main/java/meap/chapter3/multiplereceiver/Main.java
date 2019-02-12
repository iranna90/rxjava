package meap.chapter3.multiplereceiver;

import io.vertx.reactivex.core.Vertx;

public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx
        .rxDeployVerticle(Sender.class.getName())
        .subscribe(
            id -> System.out.println("deployed successfully"),
            Throwable::printStackTrace
        );

    vertx.getDelegate().deployVerticle(new Receiver(1));
    vertx.getDelegate().deployVerticle(new Receiver(2));

    System.out.println("deployment succeeded");
  }
}
