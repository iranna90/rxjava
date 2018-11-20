package book;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Chapter1 {


  public static void main(String[] args) throws InterruptedException {


    //checking();

    Observable.just(1, 2, 3, 4, 5)
        .subscribe(new Observer<Integer>() {
          private Disposable disposable;

          @Override
          public void onSubscribe(final Disposable d) {
            System.out.println("subscriber " + d.isDisposed());
            this.disposable = d;
          }

          @Override
          public void onNext(final Integer integer) {
            System.out.println("item is " + integer);

            if (integer > 3) {
              this.disposable.dispose();
            }
          }

          @Override
          public void onError(final Throwable e) {
            e.printStackTrace();
          }

          @Override
          public void onComplete() {
            System.out.println("completed");
          }
        });


    Observable.just(1, 2, 3, 4, 5)
        .takeWhile(item -> item < 4)
        .subscribe(
            item -> System.out.println("item " + item),
            Throwable::printStackTrace,
            () -> System.out.println("Completed "),
            disposable -> System.out.println("subscribed")
        );

    TimeUnit.SECONDS.sleep(1);
    System.out.println("Completed");
  }

  private static void checking() throws InterruptedException {
    System.out.println("Thread is " + Thread.currentThread().getName());


    ExecutorService service = Executors.newFixedThreadPool(1);

    Observable<Integer> integerObservable = Observable.create(subscriber -> {
          System.out.println("Emitting Thread is " + Thread.currentThread().getName());
          subscriber.onNext(1);
          subscriber.onComplete();
          /*service.submit(() -> {
            System.out.println("Emitting Thread is " + Thread.currentThread().getName());
            subscriber.onNext(1);
            subscriber.onComplete();
          });*/
        }
    );

    integerObservable
        .doOnNext(it -> System.out.println("op Thread is " + Thread.currentThread().getName()))
        .observeOn(Schedulers.computation())
        .subscribe(
            it -> System.out.println("item Thread is " + Thread.currentThread().getName()),
            Throwable::printStackTrace,
            () -> System.out.println("complete Thread is " + Thread.currentThread().getName()),
            disposable -> System.out.println("subscribed Thread is " + Thread.currentThread().getName())
        );

    System.out.println("completed Main Thread is " + Thread.currentThread().getName());

    TimeUnit.HOURS.sleep(1);
  }
}
