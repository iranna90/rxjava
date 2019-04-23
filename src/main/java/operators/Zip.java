package operators;

import io.reactivex.Observable;

public class Zip {

  public static void main(String[] args) {
    Observable<Integer> first = Observable.just(1, 2, 3, 4, 5);
    Observable<Integer> second = Observable.just(1, 2, 3);

    first.zipWith(second, (x, y) -> x * y)
        .subscribe(
            System.out::println,
            Throwable::getMessage,
            () -> System.out.println("completed"),
            dis -> System.out.println("subscribed")
        );
  }
}
