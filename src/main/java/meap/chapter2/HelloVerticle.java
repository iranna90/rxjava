package meap.chapter2;


import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

public class HelloVerticle extends AbstractVerticle {
  private final Logger logger = LoggerFactory.getLogger(HelloVerticle.class);
  private long counter = 1;

  @Override
  public void start() {
    long tick = vertx.setPeriodic(5000, id -> {
      logger.info("tick");
    });

    vertx.createHttpServer()
        .requestHandler(req -> {
          logger.info("Request #{} from {}", counter++, req.remoteAddress().host());
          req.response().end("Hello!");
        })
        .listen(8080);
    logger.info("Open http://localhost:8080/");

    System.out.println("Time id " + tick);

    vertx.setTimer(5 * 5000, id -> {
      vertx.cancelTimer(tick);
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(HelloVerticle.class.getName());
  }
}
