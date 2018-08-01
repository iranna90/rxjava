import io.reactivex.Maybe;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class SwitchCase<T, R> implements ObservableOperator<R, T> {

    final Map<T, Function<T, R>> caseBlocks = new LinkedHashMap<>();

    /**
     * create a {@link SwitchCase} operator, if any {@link Function} returned {@code null}, the
     * whole operation will crash
     *
     * @param caseBlocks the map that holds {@link Function} that are the case-blocks in this
     *                   switch-case
     */
    public SwitchCase(@NonNull Map<T, Function<T, R>> caseBlocks) {
        if (caseBlocks != null) {
            this.caseBlocks.putAll(caseBlocks);
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
            public void onNext(@NonNull T switchOn) {

                Function<T, R> block = Maybe.just(caseBlocks)
                        .filter(map -> map.containsKey(switchOn))
                        .map(map -> map.get(switchOn))
                        .blockingGet();

                if (block == null) {
                    return;
                }

                try {
                    invokeOnNext(observer, block.apply(switchOn));
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

    void invokeOnNext(Observer<? super R> observer, R onNextValue) {
        observer.onNext(onNextValue);
    }

}