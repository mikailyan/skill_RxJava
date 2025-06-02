package com.rxjavawork.schedulers;


public interface RxScheduler {

    void schedule(Runnable task);
}

