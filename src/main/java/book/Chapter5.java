package book;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;


public class Chapter5 {


  private static final ExecutorService service = Executors.newFixedThreadPool(1);

  public static void main(String[] args) throws InterruptedException {
    Observable.just(1, 2, 4)
        .delay(10, TimeUnit.SECONDS)
        .subscribe(
            it -> System.out.println(it),
            error -> error.printStackTrace(),
            () -> System.out.println("completed at " + LocalTime.now()),
            dis -> System.out.println("started at " + LocalTime.now())
        );


    TimeUnit.SECONDS.sleep(10);
  }
}
