package meap.chapter3;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.eventbus.Message;

public class LoadBalancingEvent {
  private static class Sender extends AbstractVerticle {
    @Override
    public void start(final Future<Void> startFuture) throws Exception {
      Observable.interval(1, 5, TimeUnit.SECONDS, RxHelper.scheduler(vertx))
          .take(5)
          .doOnNext(number -> System.out.println("sending message for time " + number))
          .subscribe(
              number -> vertx.eventBus().send("addr", number),
              Throwable::printStackTrace,
              () -> System.out.println("completed sending first five"),
              disposable -> {
                System.out.println("subscription started");
                startFuture.complete();
              }
          );
    }
  }


  private static class Reciever extends AbstractVerticle {

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
      vertx.eventBus().consumer("addr", message -> Observable.just(message)
          .map(Message::body)
          .subscribe(body -> System.out.println("received messages " + body), Throwable::printStackTrace));
      System.out.println("receiver deployed");
      startFuture.complete();
    }
  }
}
