package parallel.vertx;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;

public class Listener extends AbstractVerticle {

  @Override
  public void start(final Future<Void> startFuture) {
    vertx.eventBus().consumer("listen")
        .toObservable()
        .doOnNext(message ->
            System.out.println(
                "Sleeping: received item " + message.body() + " on thread " + Thread.currentThread().getName() + " at " + LocalTime
                    .now()))
        //.flatMap(it -> Observable.just(it).observeOn(Schedulers.computation()).map(this::timeConsuming))
        .subscribe(
            message -> message.reply("success"),
            Throwable::printStackTrace,
            () -> System.out.println("done "),
            disposable -> System.out.println("subscribed")
        );

    startFuture.complete();
  }

  private Message<Object> timeConsuming(final Message<Object> it) throws InterruptedException {
    System.out.println("Thread waiting " + Thread.currentThread().getName());
    TimeUnit.MILLISECONDS.sleep(1000);
    System.out.println("Thread waiting done " + Thread.currentThread().getName());
    return it;
  }
}
