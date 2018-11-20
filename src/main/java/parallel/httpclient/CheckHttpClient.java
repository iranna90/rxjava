package parallel.httpclient;

import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

public class CheckHttpClient extends AbstractVerticle {
  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(CheckHttpClient.class.getName());
  }

  private static final Set<String> URLS =
      Stream.of("http://localhost:1234/v1", "http://localhost:1234/v2", "http://localhost:1234/v3", "http://localhost:1234/v4",
          "http://localhost:1234/v5").collect(Collectors.toSet());

  WebClient webClient;

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    webClient = WebClient.create(vertx, new WebClientOptions().setMaxPoolSize(10));
    Observable.interval(10, TimeUnit.SECONDS)
        .doOnNext(it -> System.out.println(System.lineSeparator() + System.lineSeparator()))
        .flatMap(it -> Observable.fromIterable(URLS))
        .doOnNext(item -> System.out.println("Performing for url " + item + " at " + LocalTime.now()))
        .flatMap(this::httpResponse)
        .subscribe(
            data -> System.out.println("Data " + data + " at " + LocalTime.now()),
            Throwable::printStackTrace,
            () -> System.out.println("completed operation at " + LocalTime.now()),
            dispose -> System.out.println("Subscribed at " + LocalTime.now())
        );
  }

  private ObservableSource<Data> httpResponse(final String url) {

    return webClient.getAbs(url)
        .rxSend()
        .map(response -> new Data(response.statusCode(), url)).toObservable();
  }


  private static class Data {
    private final int statusCode;
    private final String url;

    private Data(final int statusCode, final String url) {
      this.statusCode = statusCode;
      this.url = url;
    }

    @Override
    public String toString() {
      return "Data{" +
          "statusCode=" + statusCode +
          ", url='" + url + '\'' +
          '}';
    }
  }
}
