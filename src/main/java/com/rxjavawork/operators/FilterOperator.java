package com.rxjavawork.operators;

import com.rxjavawork.core.RxObservable;
import com.rxjavawork.core.RxObserver;
import com.rxjavawork.core.RxDisposable;

import java.util.function.Predicate;


public class FilterOperator {
    public static <T> RxObservable<T> apply(
            RxObservable<T> source,
            Predicate<? super T> predicate
    ) {
        return RxObservable.create(observer -> {
            RxDisposable disp = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
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

