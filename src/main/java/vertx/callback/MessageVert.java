package vertx.callback;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.commons.lang3.tuple.Pair;

import static java.lang.String.format;

public class MessageVert extends AbstractVerticle {

  @Override
  public void start() {
    vertx.eventBus().<JsonObject>consumer("check")
        .toObservable()
        .map(message -> Pair.of(message, JsonObject.mapFrom(message.body())))
        .subscribe(
            tuple -> {
              System.out.println(format("Thread is %s and value is %s", Thread.currentThread().getName(), tuple.getValue()));
              JsonObject reply = tuple.getValue().put("message", "From event thread");
              Data.store(reply);
              tuple.getKey().reply(reply);
            },
            error -> {

            }
        );

    System.out.println("Message verticle deployed successfully in Thread " + Thread.currentThread().getName());
  }
}
