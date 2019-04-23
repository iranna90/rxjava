package book;


import java.time.LocalTime;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ApplyExistingCode {



  public static void main(String[] args) throws InterruptedException {

    Observable<Integer> integerObservable = ticketNumber().subscribeOn(Schedulers.io());
    Observable<String> stringObservable = flightDetails().subscribeOn(Schedulers.io());

    integerObservable.zipWith(stringObservable, ApplyExistingCode::combined)
        .flatMap(it -> it)
        .doOnNext(it -> System.out.println("Thread " + Thread.currentThread().getName() + " at " + LocalTime.now()))
        .blockingForEach(
            it -> System.out.println("Item is " + it + " and " + Thread.currentThread().getName() + " at " + LocalTime.now()));
  }

  private static Observable<Integer> ticketNumber() {
    return Observable.defer(() -> {
      System.out.println("Returning ticket number " + Thread.currentThread().getName() + " at " + LocalTime.now());
      return Observable.just(100);
    });
  }

  private static Observable<String> flightDetails() {
    return Observable.defer(() -> {
      System.out.println("Returning flight " + Thread.currentThread().getName() + " at " + LocalTime.now());
      return Observable.just("emirates");
    });
  }

  private static Observable<String> combined(int number, String flight) {
    return Observable.defer(() -> {
      System.out.println("Combined result " + Thread.currentThread().getName() + " at " + LocalTime.now());
      return Observable.just(flight + number);
    });
  }
}
