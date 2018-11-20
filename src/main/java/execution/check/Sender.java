package execution.check;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

public class Sender extends AbstractVerticle {
  @Override
  public void start(final Future<Void> startFuture) {
    vertx.eventBus().consumer("check", message -> {
      Object body = message.body();
      System.out.println("recieved at " + body);
      System.out.println("Handing message at " + Thread.currentThread().getName());
      message.reply("success");
    });

    startFuture.complete();
  }

}
