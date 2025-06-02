package com.rxjavawork.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface RxObserver<T> {

    void onNext(T item);

    void onError(Throwable t);

    void onComplete();
}

