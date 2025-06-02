package com.rxjavawork.core;


@FunctionalInterface
public interface RxOnSubscribe<T> {
    void subscribe(RxObserver<? super T> observer);
}

