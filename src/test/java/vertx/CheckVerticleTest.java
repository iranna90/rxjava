package vertx;

import java.util.concurrent.TimeUnit;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CheckVerticleTest {


  @Test
  public void test(final TestContext context) throws InterruptedException {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new CheckVerticle());

    TimeUnit.SECONDS.sleep(5);
    WebClient client = WebClient.create(vertx);
    client.getAbs("http://localhost:1234/check")
        .timeout(1000)
        .send(response -> {
          System.out.println("Is response succeeded" + response.succeeded());
          if (response.failed()){
            response.cause().printStackTrace();
          }
        });

    TimeUnit.SECONDS.sleep(10);
  }
}