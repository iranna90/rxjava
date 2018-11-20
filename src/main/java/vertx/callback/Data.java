package vertx.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.json.JsonObject;

public class Data {

  private static final Map<Integer, JsonObject> DATA = new HashMap<>();

  private static final AtomicInteger atomicInteger = new AtomicInteger(1);

  public static void store(final JsonObject entry) {
    DATA.put(atomicInteger.incrementAndGet(), entry);
  }
}
