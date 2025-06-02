package com.rxjavawork.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RxIOScheduler implements RxScheduler {
    private static final ExecutorService EXEC = Executors.newCachedThreadPool();

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}

