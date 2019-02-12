package vertx.callback;

import io.vertx.reactivex.core.Vertx;

public class MainDeploy {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(HttpVert.class.getName());
    vertx.deployVerticle(MessageVert.class.getName());
  }
}
