package examples;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static java.util.stream.Collectors.toList;

public class flatmap_switchmap {
  public static void main(final String[] args) throws InterruptedException {

    Observable.just(10, 20, 30, 40, 50)
        .delay(500, TimeUnit.MILLISECONDS)
        .flatMapMaybe(item -> {
          System.out.println("Started emmiting for item " + item);
          return fibs(item).elementAt(item);
        })
        .subscribe(item -> System.out.println("Item is " + item), Throwable::printStackTrace);

    TimeUnit.SECONDS.sleep(10);
  }

  private static Disposable switch_flat() {
    final List<String> integerStream = Stream.of("Hi", "Hello", "Done").collect(toList());

    // takes the source stream emmits the element and then maps to resulting stream and flattens resulting streams to one
    // and does not gives ypu the order of elements in flattened stream
    final Observable<String> round = Observable.interval(4000, TimeUnit.MILLISECONDS)
        .flatMap(item -> {
          System.out.println("Returning new singer for: " + item);
          return getSinger(integerStream, item);
        });

    // takes the source stream for every element it switches to new stream,
    // So ignores the resulting stream whenever new source element is emmitted previous stream will left and switched to new stream
    /*final Observable<String> round = Observable.interval(4000, TimeUnit.MILLISECONDS)
        .switchMap(item -> {
          System.out.println("Returning new singer for: " + item);
          return getSinger(integerStream, item);
        });*/


    // maps source stream to destination but maintains the order, So runs first stream and then starts next,
    // So unless first is completed next stream is not processed
    /*final Observable<String> round = Observable.interval(4000, TimeUnit.MILLISECONDS)
        .concatMap(item -> {
          System.out.println("Returning new singer for: " + item);
          return getSinger(integerStream, item);
        });*/


    Disposable subscribe = round.subscribe(System.out::println);

    System.out.println("Completed");
    return subscribe;
  }

  private static Observable<String> getSinger(final List<String> integerStream, final Long singerId) {
    return Observable
        .interval(500, TimeUnit.MILLISECONDS)
        .map(item -> {
          final Long l = item % integerStream.size();
          return l.intValue();
        })
        .map(index -> integerStream.get(index) + " from: " + singerId);
  }

  private static void checkUntil() {
    getNextInt()
        .map(item -> {
          System.out.println("before until " + item);
          return item;
        })
        .takeUntil(item -> item > 60)
        .map(item -> {
          System.out.println("after until " + item);
          return item;
        })
        .subscribe(item -> System.out.println("subscribed " + item));
  }

  private static Observable<Integer> getNextInt() {
    return Observable.create(emmitter -> {
      IntStream.rangeClosed(1, 100).boxed().peek(item -> {
        try {
          TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
          emmitter.onError(e);
        }
      }).forEach(emmitter::onNext);
      System.out.println("Done emmitting all");
      emmitter.onComplete();
    });
  }

  private static Observable<String> fibs(int number) {
    return Observable.create(subscriber -> {
      int prev = 0;
      int cur = 1;
      while (!subscriber.isDisposed()) {
        int oldPrev = prev;
        prev = cur;
        cur += oldPrev;
        String value = number + " ---> " + cur;
        System.out.println("Emiiting " + value);
        subscriber.onNext(value);
      }
    });
  }
}
