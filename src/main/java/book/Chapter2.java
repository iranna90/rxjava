package book;

import io.reactivex.Observable;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Chapter2 {
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        Observable.just(1, 2, 3, 4)
                .delay(1, TimeUnit.SECONDS)//, Schedulers.from(service))
                .subscribe(
                        it -> System.out.println("item: " + it + " Time " + LocalTime.now()),
                        Throwable::printStackTrace,
                        () -> System.out.println("Completed at: " + LocalTime.now()),
                        dis -> System.out.println("Started at: " + LocalTime.now())
                );
        LocalTime now = LocalTime.now();
        service.schedule(() -> {
            System.out.println("STarted at " + LocalTime.now());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Scheduled first at: " + now + "Running at: " + LocalTime.now());
        }, 10, TimeUnit.SECONDS);
        LocalTime later = LocalTime.now();
        service.schedule(() -> {
            System.out.println("STarted at " + LocalTime.now());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Second scheduled at: " + later + " Running at: " + LocalTime.now());
        }, 10, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(20);
    }
}
