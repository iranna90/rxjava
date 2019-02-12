package error;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class Error extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    Observable.interval(1, 1, TimeUnit.SECONDS, RxHelper.scheduler(vertx))
        .doOnNext(item -> System.out.println("Sending event for " + item))
        .doOnNext(item -> vertx.eventBus().publish("check", item))
        .subscribe(
            item -> System.out.println("sent is " + item),
            Throwable::printStackTrace
        );

    vertx.eventBus().<Long>consumer("check", message -> {
      Observable.just(message)
          .map(Message::body)
          .flatMap(item -> {
            System.out.println("item at consumer " + item);
            if (item == 5) {
              System.out.println("returinig error");
              return Observable.error(new RuntimeException("Invalid value " + item));
            }
            System.out.println("returinig success");
            return Observable.just(item);
          })
          .subscribe(
              item -> System.out.println("Item is " + item),
              Throwable::printStackTrace
          );
    });

  }

  public static void main(String[] args) {
    String deploymentId = Vertx.vertx().rxDeployVerticle(Error.class.getName()).blockingGet();
    System.out.println("deployed " + deploymentId);
  }
}
