package book;

import io.reactivex.Observable;

public class FlatMap {

    public static void main(String[] args) {
        Observable.just(1, 2, 10)
                .map(it -> {
                    if (it == 10) throw new RuntimeException("Throwing it");

                    return it;
                })
                .flatMap(
                        Observable::just,
                        error -> {
                            System.out.println("Error is: " + error.getMessage());
                            return Observable.just(30);
                        },
                        () -> Observable.just(20)
                )
                .subscribe(System.out::println);

    }
}
