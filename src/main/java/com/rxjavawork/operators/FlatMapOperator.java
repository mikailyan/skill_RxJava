package com.rxjavawork.operators;

import com.rxjavawork.core.RxCompositeDisposable;
import com.rxjavawork.core.RxDisposable;
import com.rxjavawork.core.RxObservable;
import com.rxjavawork.core.RxObserver;
import com.rxjavawork.core.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


public class FlatMapOperator {

    public static <T, R> RxObservable<R> apply(
            RxObservable<T> source,
            Function<? super T, RxObservable<? extends R>> mapper
    ) {
        return RxObservable.create(observer -> {
            RxCompositeDisposable composite = new RxCompositeDisposable();
            AtomicInteger activeCount = new AtomicInteger(1); // 1 — родительский поток
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            RxDisposable parentDisp = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    activeCount.incrementAndGet();
                    RxDisposable innerDisp = mapper.apply(item)
                            .subscribe(new RxObserver<R>() {
                                @Override
                                public void onNext(R inner) {
                                    observer.onNext(inner);
                                }
                                @Override
                                public void onError(Throwable t) {
                                    errors.add(t);
                                    completeIfDone();
                                }
                                @Override
                                public void onComplete() {
                                    completeIfDone();
                                }
                            });
                    composite.add(innerDisp);
                }

                @Override
                public void onError(Throwable t) {
                    errors.add(t);
                    completeIfDone();
                }

                @Override
                public void onComplete() {
                    completeIfDone();
                }

                private void completeIfDone() {
                    if (activeCount.decrementAndGet() == 0) {
                        // если были ошибки — передаем первую
                        Throwable err = errors.poll();
                        if (err != null) {
                            observer.onError(err);
                        } else {
                            observer.onComplete();
                        }
                        composite.dispose();
                    }
                }
            });

            composite.add(parentDisp);
        });
    }
}
