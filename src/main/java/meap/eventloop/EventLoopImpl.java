package meap.eventloop;

import java.awt.Event;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;


public final class EventLoopImpl {
  private final ConcurrentLinkedDeque<Event> events = new ConcurrentLinkedDeque<>();
  private final ConcurrentHashMap<String, Consumer<Object>> handlers = new ConcurrentHashMap<>();

  public EventLoopImpl on(String key, Consumer<Object> handler) {


    handlers.put(key, handler);
    return this;
  }

  public void dispatch(Event event) {
    events.add(event);
  }


  public void stop() {
    Thread.currentThread().interrupt();
  }

  public void run() {
    while (!(events.isEmpty() && Thread.interrupted())) {


      if (!events.isEmpty()) {
        Event event = events.pop();
        if (handlers.containsKey(event.key)) {
          handlers.get(event.key).accept(event.id);
        } else {
          System.err.println("No handler for key " + event.key);
        }
      }
    }
  }
}