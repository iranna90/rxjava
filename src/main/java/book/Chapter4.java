package book;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

public class Chapter4 {


  public static void main(String[] args) throws InterruptedException {
    checkSchedulersImmediate();

    /*Observable<String> lazy = lazy();
    System.out.println("Observable created but no subscription yet,So not executed");
    lazy.subscribe(System.out::println);*/


    // intervalCheck();

    // grouping();


    TimeUnit.SECONDS.sleep(1);
  }


  private static void grouping() {
    Observable<Integer> just = Observable.just(1, 2, 3, 4, 3, 2, 1);


    Observable<GroupedObservable<Integer, Integer>> groupedObservableObservable = just.groupBy(it -> it);


    groupedObservableObservable
        .flatMap(group -> group.count().map(it -> "total number for group: " + group.getKey() + " is: " + it).toObservable())
        .subscribe(System.out::println);
  }

  private static void intervalCheck() {
    Observable.interval(0, 100, TimeUnit.MILLISECONDS, Schedulers.io())
        .doOnNext(it -> System.out.println("Before subscriber on Thread is " + Thread.currentThread().getName()))
        .subscribe(it -> System.out.println("Thread is " + Thread.currentThread().getName()));


  }

  private static void checkSchedulersImmediate() throws InterruptedException {
    Observable.just(10, 20, 30)
        .doOnNext(it -> System.out.println("Before subscriber on Thread is " + Thread.currentThread().getName()))
        .flatMap(it -> lazy().subscribeOn(Schedulers.computation()))
        .doOnNext(it -> System.out.println("After Thread is " + Thread.currentThread().getName()))
        .subscribeOn(Schedulers.trampoline())
        .map(it -> "Hello " + it)
        .doOnNext(it -> System.out.println("After change Thread is " + Thread.currentThread().getName()))
        .subscribe(
            it -> System.out.println("Item is: " + it + System.lineSeparator() + " Thread is " + Thread.currentThread().getName())
        );


    TimeUnit.SECONDS.sleep(2);
  }


  private static Observable<String> lazy() {
    return Observable.create(subscriber -> {
      System.out.println("Running code lazy on thread " + Thread.currentThread().getName());
      subscriber.onNext("Hello");
      subscriber.onComplete();
    });
  }

}
