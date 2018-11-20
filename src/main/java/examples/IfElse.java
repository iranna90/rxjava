package examples;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class IfElse<T, R> implements ObservableOperator<R, T> {

    private final Map<Predicate<T>, Function<T, R>> blocks = new LinkedHashMap<>();

    /**
     * create a {@link IfElse} operator, if any {@link Function} returned {@code null}, the
     * whole operation will crash
     *
     * @param blocks the map that holds {@link Function} that are the if-else blocks
     */
    public IfElse(@NonNull Map<Predicate<T>, Function<T, R>> blocks) {
        if (blocks != null) {
            this.blocks.putAll(blocks);
        }
    }

    @Override
    public Observer<? super T> apply(final Observer<? super R> observer) throws Exception {
        return createResultObserver(observer);
    }

    private Observer<T> createResultObserver(final Observer<? super R> observer) {
        return new Observer<T>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull T emittedItem) {

                List<Function<T, R>> validBlocks = Observable.fromIterable(blocks.keySet())
                        .filter(key -> key.test(emittedItem))
                        .map(blocks::get)
                        .toList()
                        .flatMapMaybe(Maybe::just)
                        .blockingGet();


                if (validBlocks == null) {
                    return;
                }

                try {
                    for (Function<T, R> block : validBlocks) {
                        invokeOnNext(observer, block.apply(emittedItem));
                    }
                } catch (Throwable e) {
                    onError(e);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onComplete() {
                observer.onComplete();
            }
        };
    }

    private void invokeOnNext(Observer<? super R> observer, R onNextValue) {
        observer.onNext(onNextValue);
    }
}