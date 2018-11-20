package examples;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class Threading {

  public static void main(String[] args) throws InterruptedException {


    Observable.just("long", "longer", "longest", "longest", "longest", "longest", "longest", "longest", "longest", "longest")
        .flatMap(v ->
            performLongOperation(v)
                .doOnNext(s -> System.out.println("processing item on thread " + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.io())
        )
        .subscribe(
            length -> System.out.println("received item length " + length + " on thread " + Thread.currentThread().getName()));



    TimeUnit.SECONDS.sleep(3);
  }

  /**
   * Returns length of each param wrapped into an Observable.
   */
  protected static Observable<Integer> performLongOperation(String v) {
    Random random = new Random();
    try {
      int i = random.nextInt(3) + 1;
      System.out.println("Waiting for " + i + " seconds");
      Thread.sleep(i * 1000);
      return Observable.just(v.length());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Observable.empty();
  }
}
