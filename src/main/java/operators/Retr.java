package operators;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class Retr {

  public static void main(String[] args) {

    // retryHttpCall();

    retryForSpecificErrorAndCount();
  }

  private static void retryForSpecificErrorAndCount() {

    Observable.just(1, 2, 3, 4, 5)
        .map(it -> it)
        .retry()
        .map(it -> {
          if (it < 3) {
            return it * 20;
          } else {
            throw new IllegalArgumentException("invalid value");
          }
        })
        .flatMap(
            it -> {
              System.out.println("Item in flatmap is " + it);
              return Observable.just(it);
            },
            error -> {
              System.out.println("Error is " + error.toString());
              return Observable.just(1000);
            },
            () -> {
              System.out.println("Completed");
              return Observable.empty();
            }
        )
        .subscribe(
            it -> System.out.println("Item recieved is" + it),
            Throwable::printStackTrace,
            () -> System.out.println("completed")
        );
  }

  private static void retryHttpCall() {
    Vertx vertx = Vertx.vertx();
    WebClientOptions options = new WebClientOptions().setConnectTimeout(500);
    WebClient.create(vertx, options)
        .getAbs("http://10.23.45.67/hello")
        .rxSend()
        .doOnError(Throwable::printStackTrace)
        .retry(10)
        .delay(1, TimeUnit.SECONDS)
        .subscribe(
            response -> System.out.println(response.statusCode()),
            error -> System.out.println("Error occurred" + error.getMessage())
        );
  }
}
