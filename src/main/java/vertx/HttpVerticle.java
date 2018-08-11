package vertx;

import io.reactivex.Observable;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Future;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;

public class HttpVerticle extends AbstractVerticle {

  private CircuitBreaker circuitBreaker;

  @Override
  public void start(final Future<Void> startFuture) {
    final CircuitBreakerOptions options =
        new CircuitBreakerOptions()
            .setMaxFailures(3)
            .setTimeout(5000)
            .setResetTimeout(60 * 1000);
    circuitBreaker = CircuitBreaker
        .create("my-circuit-breaker", vertx, options)
        .fallback(this::fallback);
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/health/ready").handler(this::handleHealth);
    router.get("/health/alive").handler(rc -> rc.response().setStatusCode(200).end());
    httpServer.requestHandler(router::accept).listen(1234);
  }

  private void handleHealth(final RoutingContext rc) {

    Observable.just(rc)
        .flatMap(json -> checkClientHealth())
        .subscribe(
            suc -> {
              System.out.println("Returned the dependent component health " + suc);
              rc.response().setStatusCode(200).end();
            },
            error -> {
              System.out.println("failed " + error.getMessage());
              rc.response().setStatusCode(503).end();
            },
            () -> System.out.println("completed"),
            sub -> System.out.println("subscribed")
        );
  }

  private Observable<Boolean> checkClientHealth() {
    return circuitBreaker.rxExecuteCommand(this::execute).toObservable();
  }

  private void execute(final io.vertx.reactivex.core.Future<Boolean> future) {
    WebClient.create(vertx)
        .getAbs("http://localhost:8080/ls/v1/health/ready")
        .rxSend()
        .filter(re -> re.statusCode() == 200)
        .subscribe(
            success -> {
              System.out.println("Call completed with " + success + " in circuit breaker");
              future.complete(true);
            },
            error -> {
              error.printStackTrace();
              future.fail(error);
            },
            () -> {
              System.out.println("Response failed as did not emit any item with status code 200");
              future.fail(new RuntimeException("failed the completion, So increasing the failure count of the circuit breaker"));
            }
        );
  }

  private Boolean fallback(final Throwable error) {
    System.out.println("Circuit opened for error: " + error.getMessage());
    return true;
  }
}
