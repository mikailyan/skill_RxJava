package com.rxjavawork.core;

import com.rxjavawork.schedulers.RxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;


public class RxObservable<T> {
    private static final Logger log = LoggerFactory.getLogger(RxObservable.class);

    private final RxOnSubscribe<T> source;

    private RxObservable(RxOnSubscribe<T> source) {
        this.source = source;
    }

    public static <T> RxObservable<T> create(RxOnSubscribe<T> source) {
        log.debug("Создание RxObservable via create()");
        return new RxObservable<>(source);
    }

    public static <T> RxObservable<T> just(T item) {
        return create(observer -> {
            observer.onNext(item);
            observer.onComplete();
        });
    }

    @SafeVarargs
    public static <T> RxObservable<T> just(T... items) {
        return create(observer -> {
            Arrays.stream(items).forEach(observer::onNext);
            observer.onComplete();
        });
    }

    public RxDisposable subscribe(
            Consumer<? super T> onNext,
            Consumer<Throwable> onError,
            Runnable onComplete
    ) {
        RxObserver<T> obs = new RxObserver<T>() {
            @Override public void onNext(T item)     { onNext.accept(item); }
            @Override public void onError(Throwable t) { onError.accept(t); }
            @Override public void onComplete()       { onComplete.run(); }
        };
        return subscribe(obs);
    }

    public RxDisposable subscribe(Consumer<? super T> onNext) {
        return subscribe(onNext, Throwable::printStackTrace, () -> {});
    }

    public RxDisposable subscribe(RxObserver<? super T> observer) {
        log.debug("Новая подписка на RxObservable");
        RxDisposable disposable = new RxDisposable();
        try {
            source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposable.isDisposed()) {
                        observer.onNext(item);
                    }
                }
                @Override
                public void onError(Throwable t) {
                    if (!disposable.isDisposed()) {
                        observer.onError(t);
                    }
                }
                @Override
                public void onComplete() {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }
            });
        } catch (Throwable t) {
            observer.onError(t);
        }
        return disposable;
    }

    public RxObservable<T> subscribeOn(RxScheduler scheduler) {
        return RxObservable.create(observer ->
                scheduler.schedule(() -> this.subscribe(observer))
        );
    }

    public RxObservable<T> observeOn(RxScheduler scheduler) {
        return RxObservable.create(observer ->
                this.subscribe(new RxObserver<T>() {
                    @Override
                    public void onNext(T item) {
                        scheduler.schedule(() -> observer.onNext(item));
                    }
                    @Override
                    public void onError(Throwable t) {
                        scheduler.schedule(() -> observer.onError(t));
                    }
                    @Override
                    public void onComplete() {
                        scheduler.schedule(observer::onComplete);
                    }
                })
        );
    }
}