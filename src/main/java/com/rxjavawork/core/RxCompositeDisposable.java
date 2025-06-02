package com.rxjavawork.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class RxCompositeDisposable {
    private final Set<RxDisposable> disposables = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public void add(RxDisposable d) {
        disposables.add(d);
    }

    public void remove(RxDisposable d) {
        disposables.remove(d);
    }

    public void dispose() {
        for (RxDisposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
    }

    public boolean isDisposed() {
        return disposables.stream().allMatch(RxDisposable::isDisposed);
    }
}

