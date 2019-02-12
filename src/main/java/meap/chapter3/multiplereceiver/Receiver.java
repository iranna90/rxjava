package meap.chapter3.multiplereceiver;

import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;

public class Receiver extends AbstractVerticle {

  private final int number;

  public Receiver(int number) {
    this.number = number;
  }

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    vertx.eventBus().consumer("addr", message -> Observable.just(message)
        .map(Message::body)
        .subscribe(body -> System.out.println("received messages: " + body + " On: " + this.number + System.lineSeparator()),
            Throwable::printStackTrace));
    System.out.println("receiver deployed");
    startFuture.complete();
  }
}