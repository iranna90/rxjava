package undertanding;

import java.util.concurrent.TimeUnit;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

public class Message extends AbstractVerticle {
  @Override
  public void start(final Future<Void> startFuture) {
    vertx.eventBus().consumer("sent", message -> {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Message recieved  is " + message.body() + " Thread " + Thread.currentThread().getName());
      vertx.eventBus().publish("consume", message.body());

      message.reply("done");
    });

    System.out.println("Message deployed" + " Thread " + Thread.currentThread().getName());
  }
}
