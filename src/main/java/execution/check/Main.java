package execution.check;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.Observable;
import io.vertx.reactivex.core.Vertx;

public class Main {

  public static void main(String[] args) throws InterruptedException {


    //vertxChech();

    //creating();


    Observable.just(10, 20, 30).take(2).subscribe(System.out::println);

   /* ExecutorService service = Executors.newFixedThreadPool(1);

    Observable<Integer> obs = Observable.create(subscriber -> {

      service.submit(
          () -> {
            IntStream.rangeClosed(1, 100)
                .boxed()
                .peek(it -> {
                  try {
                    TimeUnit.MILLISECONDS.sleep(400);
                  } catch (InterruptedException e) {
                    subscriber.onError(e);
                  }
                })
                .forEach(subscriber::onNext);


            subscriber.onComplete();
            service.shutdown();
          });
    });

    obs
        .map(it -> it * 10)
        .subscribe(it -> {
          System.out.println(it);
          System.out.println("Threaad " + Thread.currentThread().getName());
        });


    System.out.println("Thread " + Thread.currentThread().getName());
    System.out.println("Thread " + Thread.currentThread().getId());*/

    TimeUnit.SECONDS.sleep(1);
  }

  private static void creating() throws InterruptedException {
    Observable<Integer> defer = Observable.defer(() -> {
      List<Integer> collect = IntStream.rangeClosed(1, 10)
          .boxed()
          .peek(it -> System.out.println("item emmitting is " + it))
          .collect(Collectors.toList());

      return Observable.fromIterable(collect);
    });

    Observable<Integer> obs = Observable.create(subscriber -> {
      IntStream.rangeClosed(1, 10)
          .boxed()
          .peek(it -> System.out.println("item emmitting is " + it))
          .forEach(it -> subscriber.onNext(it));

      subscriber.onComplete();
      System.out.println("COmpleted creation of observBLE");
    });


    multipleSubscription(defer);
  }

  private static void multipleSubscription(final Observable<Integer> obs) throws InterruptedException {
    obs.delay(100, TimeUnit.MILLISECONDS)
        .subscribe(
            nextItem -> System.out.println("item for first subscriber " + nextItem),
            Throwable::printStackTrace,
            () -> System.out.println("completed successfully"),
            sub -> System.out.println("subscriber successfully: " + sub)
        );

    TimeUnit.SECONDS.sleep(1);

    obs
        .delay(100, TimeUnit.MILLISECONDS)
        .subscribe(
            nextItem -> System.out.println("item for second subscriber " + nextItem),
            Throwable::printStackTrace,
            () -> System.out.println("completed successfully"),
            sub -> System.out.println("subscriber successfully: " + sub)
        );
  }

  private static void vertxChech() {
    Vertx vertx = Vertx.vertx();
    Observable.just(19)
        .map(it -> it)
        .filter(it -> it < 19);
    vertx.deployVerticle("execution.check.Sender");

    vertx.setPeriodic(2000, handler -> {
      System.out.println("~Thread beforre sedning " + Thread.currentThread().getName());
      vertx.eventBus().send("check", "sent", reply -> {
        System.out.println("~Thread in reply " + Thread.currentThread().getName());
        System.out.println("reply is " + reply.result().body());
      });
    });
  }

}
