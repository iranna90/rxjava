package vertxbook;

import io.reactivex.Observable;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

import java.util.concurrent.TimeUnit;

public class PointToPoint {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(Reciver.class.getName(), new DeploymentOptions().setInstances(10))
                .flatMap(id -> vertx.rxDeployVerticle(Sender.class.getName()))
                .subscribe(any -> System.out.println("Deployed successfully"));
    }

    public static class Sender extends AbstractVerticle {

        @Override
        public void start() throws Exception {
            Observable.interval(1, 1, TimeUnit.SECONDS, RxHelper.scheduler(vertx.getDelegate()))
                    .doOnNext(item -> System.out.println(System.lineSeparator()))
                    .doOnNext(item -> System.out.println("Sending item is " + item))
                    .flatMap(time -> vertx.eventBus().rxSend("check", time).toObservable())
                    .map(Message::body)
                    .subscribe(
                            body -> System.out.println("response is " + body + " Thread is " + Thread.currentThread().getName()


                            ),
                            Throwable::printStackTrace,
                            () -> System.out.println("completed successfully"),
                            dispose -> System.out.println("subscribed.")
                    );

        }
    }

    public static class Reciver extends AbstractVerticle {
        @Override
        public void start() throws Exception {
            vertx.eventBus().consumer("check", message -> {
                Observable.just(message)
                        .map(Message::body)
                        .doOnNext(body -> System.out.println("received object is " + body + " Thread " + Thread.currentThread().getName()))
                        .subscribe(
                                body -> {
                                    System.out.println("replaying to message of body " + body);
                                    message.reply("returning " + body);
                                },
                                Throwable::printStackTrace
                        );
            });
        }
    }
}
