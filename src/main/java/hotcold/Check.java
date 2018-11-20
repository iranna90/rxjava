package hotcold;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public class Check {

  public static void main(String[] args) {



    Observable<Integer> just = Observable.just(15);

    Observable<Integer> single = just.map(item -> {
      System.out.println("Item in created call " + item);
      return item;
    });



    Observable<Integer> lessThen1 = single.filter(item -> {
      System.out.println("In less then 1 condition");
      return item < 1;
    });

    ConnectableObservable<Integer> next = single.map(item -> {
      System.out.println("In next iteration " + item);
      return item;
    }).replay();

    lessThen1.switchIfEmpty(
        first(next)
            .switchIfEmpty(
                second(next)
                    .switchIfEmpty(
                        normal(next)
                    )
            )

    ).subscribe(
        System.out::println,
        Throwable::printStackTrace
    );

    next.connect();
  }

  private static Observable<Integer> normal(final Observable<Integer> next) {
    return next.map(item -> {
      System.out.println("In normal condition " + item);
      return item;
    }).filter(item -> item > 10);
  }

  private static Observable<Integer> second(final Observable<Integer> next) {
    return next.map(item -> {
      System.out.println("In second condition " + item);
      return item;
    }).filter(item -> item < 10);
  }

  private static Observable<Integer> first(final Observable<Integer> next) {
    return next.map(item -> {
      System.out.println("In first condition " + item);
      return item;
    }).filter(item -> item < 5);
  }
}
