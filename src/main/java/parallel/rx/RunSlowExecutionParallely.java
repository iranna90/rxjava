package parallel.rx;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RunSlowExecutionParallely {

  public static void main(String[] args) throws InterruptedException {



    Observable.range(1, 4)
        .doOnNext(it -> System.out.println("Running on thread " + Thread.currentThread().getName() + " before flatmap: " + it))
        .flatMap(it -> Observable.just(it).map(RunSlowExecutionParallely::slowInvocation).subscribeOn(Schedulers.computation()))
        .subscribe(
            suc -> System.out.println("Thread in subscriber is " + Thread.currentThread().getName()),
            Throwable::printStackTrace,
            () -> System.out.println("completed at " + LocalTime.now()),
            disposable -> System.out.println("started at " + LocalTime.now())
        );

    //    subscribe.dispose();

    // Observable.interval(10)

    /*Observable.just(1, 2)
        .map(it -> it * 10)
        .doOnNext(any -> System.out.println(Thread.currentThread().getName()))
        .subscribeOn(Schedulers.computation())
        .subscribe(
            item -> System.out.println("Thread is " + Thread.currentThread().getName())
        );
*/
    TimeUnit.SECONDS.sleep(11);
  }

  private static Observable<String> slowInvocation(final Integer it) throws InterruptedException {
    // int i = ThreadLocalRandom.current().nextInt(1, 10);

    System.out.println("Started sleeping for seconds: " + 10);
    TimeUnit.MILLISECONDS.sleep(10000);
    System.out.println("completed sleeping for seconds: " + 10);
    return Observable.just("Hello" + it);
  }
}
