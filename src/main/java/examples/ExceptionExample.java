package examples;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class ExceptionExample {

  public static void main(String[] args) throws InterruptedException {
    check(2);

    check(3);

    check(6);

    System.out.println("Observable completed");
    Thread.sleep(1000);
  }

  private static void check(int max) {
    Observable<Integer> obs = Observable.create(emitter -> {
      if (max > 5) {
        emitter.onError(new RuntimeException("Too high value"));
        return;
      }

      if (max > 2) {
        throw new MyExp("Value greater then allowed max 2");
      }

      emitter.onNext(max);
      emitter.onComplete();
    });

    obs
        .onErrorResumeNext(whenExceptionIs(MyExp.class, t -> Observable.just(1)))
        .subscribe(
            System.out::println,
            error -> System.out.println("Error occurred " + error.getClass().getName())
        );

  }

  private static <T> Function<? super Throwable, ObservableSource<T>> whenExceptionIs(
      final Class<? extends Throwable> clazz,
      final Function<? super Throwable, ObservableSource<T>> func
  ) {
    return throwable -> clazz.isInstance(throwable) ? func.apply(throwable) : Observable.error(throwable);
  }


  private static class MyExp extends Exception {
    private final String message;

    private MyExp(final String message) {
      super(message);
      this.message = message;
    }

    @Override
    public String getMessage() {
      return message;
    }
  }
}
