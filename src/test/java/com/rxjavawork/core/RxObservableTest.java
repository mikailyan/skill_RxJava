package com.rxjavawork.core;

import com.rxjavawork.schedulers.RxIOScheduler;
import com.rxjavawork.schedulers.RxSingleScheduler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RxObservableTest {

    @Test
    void testCreateAndSubscribe() {
        List<String> received = new ArrayList<>();
        RxObservable<String> source = RxObservable.create(observer -> {
            observer.onNext("A");
            observer.onNext("B");
            observer.onComplete();
        });

        source.subscribe(received::add,
                Throwable::printStackTrace,
                () -> received.add("DONE"));

        assertEquals(List.of("A", "B", "DONE"), received);
    }

    @Test
    void testJustSingleElement() {
        List<Integer> list = new ArrayList<>();
        RxObservable.just(42).subscribe(list::add);

        assertEquals(1, list.size());
        assertEquals(42, list.get(0));
    }

    @Test
    void testSubscribeOnScheduler() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        RxObservable.just("X")
                .subscribeOn(new RxIOScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().getName(), threadName.get());
    }

    @Test
    void testObserveOnScheduler() throws InterruptedException {
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        RxObservable.just("Y")
                .observeOn(new RxSingleScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertTrue(threadName.get().startsWith("pool-"));
    }
}

