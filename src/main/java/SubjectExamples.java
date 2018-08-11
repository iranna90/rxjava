import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class SubjectExamples {


  public static void main(String[] args) {
    async();
    behavior();
    publish();
    replay();
  }

  /* An AsyncSubject emits the last value (and only the last value) emitted by the source
   * Observable, and only after that source Observable completes. (If the source Observable
   * does not emit any values, the AsyncSubject also completes without emitting any values.)
   */
  private static void async() {

    System.out.println("async subject started");
    Subject<Integer> source = AsyncSubject.create();

    source.subscribe(item -> System.out.println("first " + item)); // it will emit only 4 and onComplete

    source.onNext(1);
    source.onNext(2);
    source.onNext(3);

    /*
     * it will emit 4 and onComplete for second observer also.
     */
    source.subscribe(item -> System.out.println("second " + item));

    source.onNext(4);
    source.onComplete();
    System.out.println("async subject conmleted");

  }

  /* When an observer subscribes to a BehaviorSubject, it begins by emitting the item most
   * recently emitted by the source Observable (or a seed/default value if none has yet been
   * emitted) and then continues to emit any other items emitted later by the source Observable(s).
   * It is different from Async Subject as async emits the last value (and only the last value)
   * but the Behavior Subject emits the last and the subsequent values also.
   */
  private static void behavior() {

    System.out.println("behavious subject started");
    BehaviorSubject<Integer> source = BehaviorSubject.create();

    source.subscribe(item -> System.out.println("first " + item)); // it will get 1, 2, 3, 4 and onComplete

    source.onNext(1);
    source.onNext(2);
    source.onNext(3);

    /*
     * it will emit 3(last emitted), 4 and onComplete for second observer also.
     */
    source.subscribe(item -> System.out.println("second " + item));

    source.onNext(4);
    source.onComplete();
    System.out.println("behavious subject completed");

  }

  /* PublishSubject emits to an observer only those items that are emitted
   * by the source Observable, subsequent to the time of the subscription.
   */
  private static void publish() {

    System.out.println("publish started");
    PublishSubject<Integer> source = PublishSubject.create();

    source.subscribe(item -> System.out.println("first " + item)); // it will get 1, 2, 3, 4 and onComplete

    source.onNext(1);
    source.onNext(2);
    source.onNext(3);

    /*
     * it will emit 4 and onComplete for second observer also.
     */
    source.subscribe(item -> System.out.println("second " + item));

    source.onNext(4);
    source.onComplete();
    System.out.println("publish completed");

  }

  /* ReplaySubject emits to any observer all of the items that were emitted
   * by the source Observable, regardless of when the observer subscribes.
   */
  private static void replay() {

    System.out.println("reply started");
    ReplaySubject<Integer> source = ReplaySubject.create();

    source.subscribe(item -> System.out.println("first " + item)); // it will get 1, 2, 3, 4

    source.onNext(1);
    source.onNext(2);
    source.onNext(3);
    source.onNext(4);
    source.onComplete();

    /*
     * it will emit 1, 2, 3, 4 for second observer also as we have used replay
     */
    source.subscribe(item -> System.out.println("second " + item));
    System.out.println("reply completed");

  }
}
