package vertx.callback;

import io.vertx.reactivex.core.Vertx;

public class MainDeploy {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle("vertx.callback.HttpVert");
    vertx.deployVerticle("vertx.callback.MessageVert");
  }
}
