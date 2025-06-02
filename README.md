# RxJava

**Кастомная реализация** основных концепций реактивного программирования на Java (аналог RxJava).

## Описание проекта

В проекте реализована система реактивных потоков с возможностью управления потоками выполнения и обработки событий, построенная на паттерне «Наблюдатель» (Observer). Реализованы базовые компоненты, операторы преобразования данных, планировщики потоков и механизмы отмены подписки.


## Основные функции

* **RxObservable** — источник данных, фабрики `create()`, `just()`.
* **RxObserver** — интерфейс с методами `onNext()`, `onError()`, `onComplete()`.
* **Операторы** (в пакете `com.rxjavawork.operators`):

    * `MapOperator` (`map`)
    * `FilterOperator` (`filter`)
    * `FlatMapOperator` (`flatMap`)
    * `MergeOperator` (`merge`)
    * `ConcatOperator` (`concat`)
    * `ReduceOperator` (`reduce`)
* **Schedulers** (в пакете `com.rxjavawork.schedulers`):

    * `RxIOScheduler` (cached thread pool)
    * `RxComputationScheduler` (fixed thread pool)
    * `RxSingleScheduler` (single-thread executor)
* **Disposable**:

    * `RxDisposable` — отмена одной подписки
    * `RxCompositeDisposable` — групповая отмена
* **Логирование** через SLF4J + Log4j

## Архитектура системы

1. **Паттерн Observer**:

    * Источник (`RxObservable`) делегирует эмиссию элементов через `RxOnSubscribe`.
    * Потребитель реализует `RxObserver` или передаёт лямбды в `subscribe()`.
    * `RxDisposable` контролирует отмену, `RxCompositeDisposable` — групповую отмену.

2. **Структура пакетов**:

    * `core` — базовые компоненты и фабрики.
    * `operators` — классы-операторы для модульности.
    * `schedulers` — управление планировщиками потоков.

3. **Flow**:

    * Построение цепочки: `RxObservable.create(...)` → операторы → `subscribeOn()`/`observeOn()` → `subscribe()`.
    * Все переходы потоков выполняются через `RxScheduler.schedule(...)`.

## Принципы работы Schedulers

| Scheduler                  | Реализация               | Применение                 |
| -------------------------- | ------------------------ | -------------------------- |
| **RxIOScheduler**          | `CachedThreadPool`       | I/O задачи, сеть           |
| **RxComputationScheduler** | `FixedThreadPool(N=CPU)` | CPU-bound вычисления       |
| **RxSingleScheduler**      | `SingleThreadExecutor`   | Последовательная обработка |

* `subscribeOn()` определяет поток подписки.
* `observeOn()` переключает поток обработки событий.

## Тестирование

В проекте написаны юнит-тесты JUnit 5 для ключевых сценариев:

1. **Базовая работа**

    * `create()` + `subscribe(onNext, onError, onComplete)`
    * `just()`, проверка эмиссии и завершения.
2. **Операторы**

    * `map`, `filter`
    * `flatMap`, `merge`, `concat`, `reduce`
3. **Планировщики**

    * `subscribeOn`/`observeOn` проверяют переключение потоков.
4. **Обработка ошибок**

    * Эмит `onError`, проверка прекращения `onNext`.
5. **Отмена подписки**

    * `RxDisposable.dispose()`, `RxCompositeDisposable.dispose()`.

## Примеры использования

```bash
// map + filter + планировщики
MapOperator.apply(
    RxObservable.just(1,2,3,4,5),
    i -> i * 2
)
.subscribeOn(new RxIOScheduler())
.observeOn(new RxSingleScheduler())
.subscribe(
    i -> System.out.println("-> " + i),
    Throwable::printStackTrace,
    () -> System.out.println("Done")
);

// flatMap
FlatMapOperator.apply(
    RxObservable.just("A","B"),
    s -> RxObservable.just(s + "1", s + "2")
).subscribe(System.out::println);
```
