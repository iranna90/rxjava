package repeat_retry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.Single;

public class Repeat {


  public static void main(String[] args) throws InterruptedException {
    // repeat();

    repeatWhen();

    Thread.sleep(11 * 1000);
  }

  private static void repeatWhen() {
    AtomicInteger in = new AtomicInteger();
    Observable.just(10, 20, 30, 40, 50, 60, 70)
        .delay(1000, TimeUnit.MILLISECONDS)
        .map(item -> {
          System.out.println("before condition " + item);
          return item;
        })
        .repeatWhen(notification -> notification.flatMap(i -> {

          System.out.println("mapped value " + i + " type is " + i.getClass().getName());
          if (in.getAndAdd(1) < 3) {
            System.out.println("Returning observable of 70 for " + i);
            return Observable.just(i);
          } else {
            System.out.println("Returning empty");
            return Observable.empty();
          }
        }))
        .subscribe(
            item -> System.out.println("Item is " + item),
            error -> error.printStackTrace(),
            () -> System.out.println("completed")
        );

  }

  private static void repeat() {
    Single.just(10)
        .delay(1000, TimeUnit.MILLISECONDS)
        .map(item -> {
          System.out.println("before repeat");
          return item;
        })
        .repeat(5)
        .map(item -> {
          System.out.println("After repeat ");
          return item + " hello";
        })
        .subscribe(
            suc -> System.out.println("ScuccessValue " + suc),
            error -> error.printStackTrace(),
            () -> System.out.println("completed")/*,
            sub -> System.out.println("subscribed")*/
        );
  }
}
