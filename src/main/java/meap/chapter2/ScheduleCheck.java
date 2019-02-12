package meap.chapter2;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleCheck {

  public static void main(String[] args) {
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.scheduleAtFixedRate(() -> System.out.println("running periodic schedule " + LocalTime.now()), 5, 5, TimeUnit.SECONDS);

    scheduledExecutorService.schedule(() -> System.out.println("running one time schedule " + LocalTime.now()), 1000, TimeUnit.MILLISECONDS);

    System.out.println("Completed");
  }
}
