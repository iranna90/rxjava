package meap.chapter2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxEcho extends AbstractVerticle {

  private static int numberOfConnections = 0;

  @Override
  public void start() throws Exception {
    vertx.createNetServer()
        .connectHandler(VertxEcho::handleNewClient)
        .listen(3000);

    vertx.setPeriodic(5000, id -> System.out.println(howMany()));

    vertx.createHttpServer()
        .requestHandler(request -> request.response().end(howMany()))
        .listen(8080);

    System.out.println("Started verticle on thread " + Thread.currentThread().getName());
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new VertxEcho());
  }

  private static void handleNewClient(NetSocket socket) {
    numberOfConnections++;
    socket.handler(buffer -> {
      buffer.appendBytes(Thread.currentThread().getName().getBytes());
      buffer.appendBytes("\n".getBytes());
      socket.write(buffer);
      if (buffer.toString().endsWith("/quit\n")) {
        socket.close();
      }
    });
    socket.closeHandler(v -> numberOfConnections--);
  }

  private static String howMany() {
    return "We now have " + numberOfConnections + " connections and thread is " + Thread.currentThread().getName();
  }
}