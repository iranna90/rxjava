package vertx.failure.check;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

public class EventV extends AbstractVerticle {

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    vertx.eventBus().consumer("check")
        .toObservable()
        .map(message -> ((Message<JsonObject>) message))
        .subscribe(
            message -> message.reply(message.body().put("from_event", "Hello")),
            erorr -> {
              System.out.println("Error in event " + erorr.getMessage());
              erorr.printStackTrace();
              // message.fail(10000, "error " + erorr.getMessage());
            },
            () -> System.out.println("completed event")
        );

    startFuture.complete();
  }
}
