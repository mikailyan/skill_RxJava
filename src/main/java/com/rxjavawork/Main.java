package com.rxjavawork;

import com.rxjavawork.core.RxObservable;
import com.rxjavawork.operators.*;
import com.rxjavawork.schedulers.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Пример 1: map + filter + subscribeOn/observeOn
        RxObservable<Integer> source = RxObservable.just(1, 2, 3, 4, 5);
        System.out.println("=== map & filter с планировщиками ===");
        FilterOperator.apply(
                        MapOperator.apply(source, i -> i * 10),
                        i -> i >= 30
                )
                .subscribeOn(new RxIOScheduler())
                .observeOn(new RxComputationScheduler())
                .subscribe(
                        i -> System.out.println("Received: " + i),
                        Throwable::printStackTrace,
                        () -> System.out.println("Completed\n")
                );

        // Дадим асинхронным задачам завершиться
        Thread.sleep(500);

        // Пример 2: flatMap
        System.out.println("=== flatMap пример ===");
        FlatMapOperator.apply(source, i ->
                RxObservable.just(i, i * i)
        ).subscribe(i -> System.out.println("flatMap: " + i));

        // Пример 3: merge
        System.out.println("\n=== merge пример ===");
        MergeOperator.apply(
                RxObservable.just("A", "B"),
                RxObservable.just("1", "2")
        ).subscribe(s -> System.out.println("merge: " + s));

        // Пример 4: concat
        System.out.println("\n=== concat пример ===");
        ConcatOperator.apply(
                RxObservable.just("X", "Y"),
                RxObservable.just("Z")
        ).subscribe(s -> System.out.println("concat: " + s));
    }
}
