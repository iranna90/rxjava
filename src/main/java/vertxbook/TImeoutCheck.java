package vertxbook;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

import java.util.concurrent.TimeUnit;

public class TImeoutCheck {

    public static class Sender extends AbstractVerticle {
        @Override
        public void start() throws Exception {
            final DeliveryOptions options = new DeliveryOptions().setSendTimeout(1000);
            Observable.interval(1, 1, TimeUnit.SECONDS, RxHelper.scheduler(vertx.getDelegate()))
                    .flatMap(it -> sendMessage(options, it))
                    .subscribe(
                            it -> System.out.println("Response is: " + it),
                            Throwable::printStackTrace
                    );
        }

        private Observable<Object> sendMessage(DeliveryOptions options, Long it) {
            return vertx.eventBus().rxSend("check", it, options)
                    .map(Message::body).toObservable()
                    .onErrorResumeNext(thr -> {
                        System.out.println("Error occurred " + thr.getMessage());
                        return Observable.empty();
                    });
        }
    }


    public static class Rec extends AbstractVerticle {
        @Override
        public void start() throws Exception {
            vertx.eventBus().consumer("check", message -> {
                Observable.just(message)
                        .doOnNext(it -> System.out.println("Before sleeping the item is: " + it.body()))
                        .delay(2, TimeUnit.SECONDS, Schedulers.io())
                        .map(Message::body)
                        .doOnNext(it -> System.out.println("Itemmmmmmm recieved is " + it))
                        .doOnError(error -> System.out.println("Error is " + error + " for item " + message.body()))
                        .subscribe(
                                it -> {
                                    System.out.println("replying with message " + it);
                                    message.reply(it);
                                },
                                error -> {
                                    error.printStackTrace();
                                    message.fail(1, error.getMessage());
                                }
                        );
            });
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(Rec.class.getName())
                .flatMap(id -> vertx.rxDeployVerticle(Sender.class.getName()))
                .subscribe(
                        suc -> System.out.println("Deployment succeeded")
                );
    }
}
