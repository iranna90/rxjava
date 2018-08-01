import io.vertx.reactivex.core.AbstractVerticle;

public class Consume extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("check").toObservable()
        .map(message -> {
          System.out.println("message returned is " + message.body().toString());
          return message;
        }).subscribe(
        message -> {
          System.out.println("message subscribed is " + message.body().toString());
          message.reply("pong");
        },
        Throwable::printStackTrace
    );
  }
}