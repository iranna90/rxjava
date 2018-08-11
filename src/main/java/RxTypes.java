import io.reactivex.Maybe;
import io.reactivex.Single;

public class RxTypes {

  public static void main(String[] args) throws InterruptedException {
    mayBe(Maybe.just(10));
    mayBe(Maybe.empty());


    single(Single.just(20));
    single(Single.error(new RuntimeException("No element found")));

    System.out.println("completed main call");
    Thread.sleep(1000);
  }

  private static void mayBe(Maybe<Integer> maybe) {
    maybe
        .subscribe(
            System.out::println,
            Throwable::printStackTrace,
            () -> System.out.println("Completed with empty")
        );
  }

  private static void single(Single<Integer> single) {
    single
        .subscribe(
            System.out::println,
            Throwable::printStackTrace
        );
  }
}
