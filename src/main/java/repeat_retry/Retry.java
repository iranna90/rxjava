package repeat_retry;


import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import io.reactivex.Observable;

public class Retry {

  public static void main(String[] args) {
     retryWhen();
   // zipCheck();
  }

  private static void retryWhen() {
    System.out.println("retry ");

    Observable.create(emmiter -> {
      System.out.println("creating");
      IntStream.rangeClosed(1, 5).boxed().forEach(emmiter::onNext);

      System.out.println("Thrwing erorr");
      emmiter.onError(new RuntimeException("fucked up"));
    })
        .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
          System.out.println("delay retry by " + i + " second(s)");
          return Observable.timer(i, TimeUnit.SECONDS);
        })).blockingForEach(
            item -> System.out.println(item));
  }

  private static void zipCheck() {
    Observable<Integer> ob1 = Observable.just(10, 20, 30);
    Observable<Integer> ob2 = Observable.just(10, 20, 30, 40);

    ob1.zipWith(ob2, (f, s) -> f + s)
        .subscribe(
            System.out::println
        );
  }
}
