# SF_RxJavaWork

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

## Технологии

* Java 17+
* Maven
* SLF4J API + Log4j
* JUnit 5

## Установка и запуск

1. Клонировать репозиторий:

   ```bash
   git clone https://github.com/ВАШ_ПРОЕКТ/rxjavawork.git
   cd rxjavawork
   ```
2. Собрать и запустить тесты:

   ```bash
   mvn clean test
   ```
3. Запустить демонстрацию:

   ```bash
   mvn exec:java -Dexec.mainClass="com.rxjavawork.Main"
   ```

## Структура проекта

```plaintext
rxjavawork/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rxjavawork/
│   │   │           ├── core/
│   │   │           │   ├── RxObservable.java
│   │   │           │   ├── RxObserver.java
│   │   │           │   ├── RxOnSubscribe.java
│   │   │           │   ├── RxDisposable.java
│   │   │           │   └── RxCompositeDisposable.java
│   │   │           ├── operators/
│   │   │           │   ├── MapOperator.java
│   │   │           │   ├── FilterOperator.java
│   │   │           │   ├── FlatMapOperator.java
│   │   │           │   ├── MergeOperator.java
│   │   │           │   ├── ConcatOperator.java
│   │   │           │   └── ReduceOperator.java
│   │   │           ├── schedulers/
│   │   │           │   ├── RxScheduler.java
│   │   │           │   ├── RxIOScheduler.java
│   │   │           │   ├── RxComputationScheduler.java
│   │   │           │   └── RxSingleScheduler.java
│   │   │           └── Main.java
│   │   └── resources/
│   │       └── log4j.properties
│   └── test/
│       └── java/
│           └── com/
│               └── rxjavawork/
│                   ├── core/
│                   │   └── RxObservableTest.java
│                   ├── operators/
│                   │   └── OperatorTest.java
│                   └── schedulers/
│                       └── SchedulerTest.java
```

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

Запуск:

```bash
mvn test
```

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

## Автор

Березняк Владимир

Проект реализован в рамках учебного задания МИФИ

GitHub: github.com/amasovich

Telegram: @amasovich
