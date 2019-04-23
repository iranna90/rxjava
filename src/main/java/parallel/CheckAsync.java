package parallel;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CheckAsync {

  public static void main(String[] args) throws InterruptedException {
    //schedulesSequentially();

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    scheduledExecutorService.schedule(() -> {
          System.out.println("first Scheduled " + Thread.currentThread().getName());
          try {
            TimeUnit.SECONDS.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("Completed 1 at " + LocalTime.now());
        },
        0l,
        TimeUnit.NANOSECONDS
    );

    scheduledExecutorService.schedule(() -> {
          System.out.println("second Scheduled " + Thread.currentThread().getName());
          try {
            TimeUnit.SECONDS.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("Completed at " + LocalTime.now());
        },
        0l,
        TimeUnit.NANOSECONDS
    );

    scheduledExecutorService.schedule(() -> {
          System.out.println("third Scheduled " + Thread.currentThread().getName());
          try {
            TimeUnit.SECONDS.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("Completed 3 at " + LocalTime.now());
        },
        0l,
        TimeUnit.NANOSECONDS
    );

  }

  private static void schedulesSequentially() throws InterruptedException {
    Observable.just(1, 2, 3, 4)
        .doOnNext(it -> System.out.println("item is " + it + " at " + LocalTime.now()))
        .observeOn(Schedulers.io())
        .map(CheckAsync::doDelayed)
        //.flatMap(it -> Observable.just(it).observeOn(Schedulers.io()).map(CheckAsync::doDelayed))
        .subscribe(
            it -> System.out
                .println("Recived item " + it + "at " + LocalTime.now() + " thread " + Thread.currentThread().getName()),
            Throwable::printStackTrace,
            () -> System.out.println("Completed at " + LocalTime.now()),
            subscription -> System.out.println("Started at " + LocalTime.now() + " on thread " + Thread.currentThread().getName())
        );

    TimeUnit.SECONDS.sleep(50 * 5);
  }

  private static int doDelayed(final Integer it) throws InterruptedException {
    System.out.println("Scheduled by thread " + Thread.currentThread().getName());
    TimeUnit.SECONDS.sleep(50);
    System.out.println("Completed by thread " + Thread.currentThread().getName());
    return it;
  }
}
