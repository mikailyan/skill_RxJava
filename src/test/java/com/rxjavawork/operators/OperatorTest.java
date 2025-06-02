package com.rxjavawork.operators;

import com.rxjavawork.core.RxObservable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @Test
    void testMapAndFilter() {
        RxObservable<Integer> src = RxObservable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onComplete();
        });

        List<Integer> result = new ArrayList<>();
        // сначала map, затем filter
        RxObservable<Integer> mapped = MapOperator.apply(src, i -> i * 10);
        RxObservable<Integer> filtered = FilterOperator.apply(mapped, i -> i > 10);
        filtered.subscribe(result::add);

        assertEquals(List.of(20, 30), result);
    }

    @Test
    void testReduce() {
        RxObservable<Integer> src = RxObservable.create(o -> {
            o.onNext(2);
            o.onNext(3);
            o.onNext(5);
            o.onComplete();
        });

        List<Integer> out = new ArrayList<>();
        RxObservable<Integer> reduced = ReduceOperator.apply(src, (a, b) -> a + b);
        reduced.subscribe(out::add, Throwable::printStackTrace, () -> {});

        assertEquals(List.of(10), out);
    }

    @Test
    void testMerge() {
        RxObservable<String> a = RxObservable.create(o -> {
            o.onNext("A1");
            o.onNext("A2");
            o.onComplete();
        });
        RxObservable<String> b = RxObservable.create(o -> {
            o.onNext("B1");
            o.onComplete();
        });

        List<String> merged = new ArrayList<>();
        RxObservable<String> mergedObs = MergeOperator.apply(a, b);
        mergedObs.subscribe(merged::add, Throwable::printStackTrace, () -> {});

        // Все элементы должны быть присутствовать
        assertTrue(merged.containsAll(List.of("A1", "A2", "B1")));
        assertEquals(3, merged.size());
    }

    @Test
    void testConcat() {
        RxObservable<String> a = RxObservable.create(o -> {
            o.onNext("1");
            o.onComplete();
        });
        RxObservable<String> b = RxObservable.create(o -> {
            o.onNext("2");
            o.onComplete();
        });

        List<String> out = new ArrayList<>();
        RxObservable<String> concatObs = ConcatOperator.apply(a, b);
        concatObs.subscribe(out::add);

        assertEquals(List.of("1", "2"), out);
    }

    @Test
    void testFlatMap() {
        RxObservable<Integer> src = RxObservable.<Integer>create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        });

        List<Integer> out = new ArrayList<>();
        // теперь просто используем varargs-just:
        RxObservable<Integer> flat = FlatMapOperator.apply(src, i ->
                RxObservable.just(i, i * 10)
        );
        flat.subscribe(out::add);

        assertEquals(4, out.size());
        assertTrue(out.containsAll(List.of(1, 10, 2, 20)));
    }
}
