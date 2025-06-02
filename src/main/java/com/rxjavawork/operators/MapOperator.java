package com.rxjavawork.operators;

import com.rxjavawork.core.RxObservable;
import com.rxjavawork.core.RxObserver;
import com.rxjavawork.core.RxDisposable;

import java.util.function.Function;


public class MapOperator {
    public static <T, R> RxObservable<R> apply(
            RxObservable<T> source,
            Function<? super T, ? extends R> mapper
    ) {
        return RxObservable.create(observer -> {
            RxDisposable disp = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    observer.onNext(mapper.apply(item));
                }
                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }
                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        });
    }
}

