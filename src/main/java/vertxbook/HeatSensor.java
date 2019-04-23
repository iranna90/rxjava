package vertxbook;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.vertx.core.Future;
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HeatSensor {

    private static final int BASE_TEMP = 20;
    private static final String TEMP_ADDRESS = "temperature.update";
    private static final String TEMP_AVERAGE = "temperature.average";
    private static final List<String> MESSAGES = new ArrayList<>();
    private static final List<Integer> TEMPERATURES = new ArrayList<>();
    private static int average = 0;

    public static class HeatProducer extends AbstractVerticle {
        @Override
        public void start(Future<Void> startFuture) throws Exception {
            Observable.interval(1, 1, TimeUnit.SECONDS, RxHelper.scheduler(vertx.getDelegate()))
                    .map(any -> toTemp())
                    .doOnNext(temp -> vertx.eventBus().publish(TEMP_ADDRESS, temp))
                    .subscribe(
                            temp -> System.out.println("temperature is " + temp),
                            Throwable::printStackTrace,
                            () -> System.out.println("Completed"),
                            dis -> startFuture.complete()
                    );
        }

        private int toTemp() {
            return BASE_TEMP + ThreadLocalRandom.current().nextInt(1, 20);
        }
    }


    public static class DataStore extends AbstractVerticle {
        @Override
        public void start(Future<Void> startFuture) throws Exception {
            vertx.eventBus().consumer(
                    TEMP_ADDRESS,
                    message -> Observable.just(message)
                            .map(Message::body)
                            .map(temp -> (int) temp)
                            .doOnNext(this::updateAverage)
                            .map(temp -> "Temperature at: " + LocalTime.now() + " Is: " + temp)
                            .doOnNext(MESSAGES::add)
                            .subscribe(msg -> System.out.println("Added successfully " + msg), Throwable::printStackTrace)
            );

            vertx.eventBus().consumer(TEMP_AVERAGE, message -> message.reply("Average is " + average));

            startFuture.complete();
        }

        private void updateAverage(Integer temp) {
            TEMPERATURES.add(temp);
            average = (int) TEMPERATURES.stream().mapToInt(it -> it).average().orElse(0);
        }
    }

    public static class Listener extends AbstractVerticle {
        @Override
        public void start(Future<Void> startFuture) throws Exception {
            vertx.eventBus().consumer(
                    TEMP_ADDRESS,
                    message -> Observable.just(message)
                            .map(Message::body)
                            .subscribe(it -> System.out.println("Temperature is " + it))
            );

            startFuture.complete();
        }
    }

    public static class Http extends AbstractVerticle {
        @Override
        public void start(Future<Void> startFuture) throws Exception {
            HttpServer httpServer = vertx.createHttpServer();
            Router router = Router.router(vertx);
            router.get("/").handler(this::handleHttpReq);
            router.get("/sse").handler(this::sse);


            httpServer.requestHandler(router::accept)
                    .rxListen(1234)
                    .subscribe(
                            suc -> startFuture.complete(),
                            startFuture::fail
                    );
        }

        private void handleHttpReq(RoutingContext rc) {
            vertx.eventBus().<String>rxSend(TEMP_AVERAGE, "any").map(Message::body).toObservable()
                    .subscribe(
                            result -> rc.response().end(result),
                            error -> rc.response().end(error.getMessage())
                    );
        }

        private void sse(RoutingContext routingContext) {

            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("Content-Type", "text/event-stream")
                    .putHeader("Cache-Control", "no-cache")
                    .setChunked(true);

            final MessageConsumer<Integer> tempConsumer = vertx.eventBus().consumer(TEMP_ADDRESS, message -> Single.just(message).map(Message::body)
                    .map(temp -> "Present temperature is: " + temp + System.lineSeparator())
                    .subscribe(response::write, Throwable::printStackTrace)
            );

            Disposable subscribe = Observable.interval(5, 5, TimeUnit.SECONDS, RxHelper.scheduler(vertx.getDelegate()))
                    .flatMap(any -> vertx.eventBus().<String>rxSend(TEMP_AVERAGE, any).map(Message::body).toObservable())
                    .map(avg -> avg + System.lineSeparator())
                    .subscribe(
                            response::write,
                            error -> {
                                response.write(error.getMessage());
                                response.end("error occurred");
                            },
                            () -> response.end("completed successfully")
                    );

            routingContext.response().endHandler(hnad -> {
                        System.out.println("End handle is calling");
                        tempConsumer.unregister();
                        subscribe.dispose();
                    }

            );
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(DataStore.class.getName())
                .flatMap(any -> vertx.rxDeployVerticle(Listener.class.getName()))
                .flatMap(any -> vertx.rxDeployVerticle(Http.class.getName()))
                .flatMap(any -> vertx.rxDeployVerticle(HeatProducer.class.getName()))
                .subscribe(suc -> System.out.println("Successfully deployed all vert"), Throwable::printStackTrace);
    }
}
