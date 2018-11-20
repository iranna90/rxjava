package parallel.vertx;

import io.vertx.core.VertxOptions;
import io.vertx.reactivex.core.Vertx;

public class Main {
  private static final long nano = (long) Math.pow(10, 9);

  public static void main(String[] args) {

    System.out.println("time is " + nano);
    long time = 6 * nano;
    Vertx vertx = Vertx.vertx(
        new VertxOptions().setMaxEventLoopExecuteTime(time).setWarningExceptionTime(time).setBlockedThreadCheckInterval(time));

    vertx.rxDeployVerticle("parallel.vertx.Listener").flatMap(httpId -> vertx.rxDeployVerticle("parallel.vertx.Http"))
        .subscribe(suc -> System.out.println("succeesfully deployed"), Throwable::printStackTrace);
  }
}
