package com.rxjavawork.core;

import java.util.concurrent.atomic.AtomicBoolean;


public class RxDisposable {
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    public void dispose() {
        disposed.set(true);
    }

    public boolean isDisposed() {
        return disposed.get();
    }
}

