package practice;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static practice.Out.errPrintln;
import static practice.Out.println;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

public class FluxCore {

    public void doPractice() {

        println("\nemptyFlux:");
        emptyFlux();

        println("\nfooBarFluxFromValues:");
        fooBarFluxFromValues();

        println("\nfooBarFluxFromList:");
        fooBarFluxFromList();

        println("\nerrorFlux:");
        errorFlux();

        println("\nnumbersFrom5To7:");
        numbersFrom5To7();

        println("\nfilterAndCapitalize:");
        filterAndCapitalize();

        println("\ncounterFor10ValuesEach100ms:");
        counterFor10ValuesEach100ms();

        println("\ncollectCounterFor10ValuesEach100ms:");
        collectCounterFor10ValuesEach100ms();

        println("\ncapitalizeAsync:");
        capitalizeAsync();

        println("\nmerge:");
        merge();

        println("\nconcat:");
        concat();

        println("\nfrom monos:");
        fromMonos();

        println("\ncombineLatest:");
        combineLatest();

        println("\nonErrorReturn:");
        onErrorReturn();

        println("\ndealWithCheckedExceptions:");
        dealWithCheckedExceptions();

        println("\ndealWithErrors:");
        dealWithErrors();

        println("\ndealWithSinks:");
        dealWithSinks();

        println("\nzip:");
        zip();

        println("\nignoreElements:");
        ignoreElements();

        println("\nfluxCompletion:");
        fluxCompletion();

        println("\ntoIterable:");
        toIterable();

        println("\nblocking repository to flux:");
        blockingRepositoryToFlux();

        println("\nmakeBlockingCall:");
        makeBlockingCall();

        println("\nfluxToBlockingRepository:");
        fluxToBlockingRepository();

        println("\nexponentialBackoff:");
        exponentialBackoff();

        println("\nuseDisposable:");
        useDisposable();

        println("\nbatchSubscriber:");
        batchSubscriber();

        println("\nsimpleSubscriber:");
        simpleSubscriber();

        println("\ncancelSubscriber:");
        cancelSubscriber();

        println("\nlimitRate:");
        limitRate();

        println("\nlimitRequest:");
        limitRequest();

        println("\nsyncGenerate:");
        syncGenerate();

        println("\ncreateWithDropStrategy: drops values if producer is faster than consumer and there is a queue overflow");
        createWithDropStrategy();

        println("\ncreateWithLatestStrategy: takes latest value if producer is faster than consumer and there is a queue overflow");
        createWithLatestStrategy();

        println("\ncreateWithErrorStrategy: throw an error if producer is faster than consumer and there is a queue overflow");
        createWithErrorStrategy();

        println("\ncreateWithIgnoreStrategy: continue to produce but stop consume and throw an error if producer is faster than consumer and there is a queue overflow");
        createWithIgnoreStrategy();

        println("\ncreateWithBufferStrategy: buffer values if producer is faster than consumer");
        createWithBufferStrategy();

        println("\nhybridPushPull:");
        hybridPushPull();

        println("\nhandle:");
        handle();

        println("\npublishOnParallelScheduler:");
        publishOnParallelScheduler();

        println("\npublishOnSingleScheduler:");
        publishOnSingleScheduler();

        println("\nsubscribeOnParallelScheduler:");
        subscribeOnParallelScheduler();

        println("\nonErrorReturn:");
        onErrorReturn();

        println("\ntransform:");
        transform();

        println("\ntransformDeferred:");
        transformDeferred();

        println("\nhot publisher just:");
        hotPublisherJust();

        println("\nhot publisher using sinks:");
        hotPublisherUsingSinks();

        println("\ncold publisher:");
        coldPublisher();

        println("\nconnectableFlux:");
        connectableFlux();

        println("\nautoConnect:");
        autoConnect();

        println("\ngroupBy:");
        groupBy();

        println("\nwindows:");
        windows();

        println("\nbuffer:");
        buffer();

        println("\nbufferWhile:");
        bufferWhile();

        println("\nparallel:");
        useParallel();

        println("\nreplaceSchedulers:");
        replaceSchedulers();

        println("\nflux practice finished:");
    }

    private void emptyFlux() {
        Flux.empty()
                .subscribe(Out::println);
    }

    private void fooBarFluxFromValues() {
        Flux.just("foo", "bar")
                .subscribe(Out::println);
    }

    private void fooBarFluxFromList() {
        var fooBar = List.of("foo", "bar");

        Flux.fromIterable(fooBar)
                .subscribe(Out::println, null, () -> println("fooBar COMPLETED!!!"));
    }

    private void errorFlux() {
        Flux.error(new IllegalStateException())
                .subscribe(Out::println, Out::errPrintln);
    }

    private void numbersFrom5To7() {
        Flux.range(5, 3)
                .subscribe(Out::println);

    }

    private void filterAndCapitalize() {
        Flux.<String>create(emitter -> {
                    emitter.next("hello, world");
                    emitter.next("hello, country");
                    emitter.next("hello, region");
                    emitter.next("hello, city");
                    emitter.next("hello, street");
                    emitter.next("hello, house");
                    emitter.next("hello, apartment");

                    emitter.complete();
                })
                .map(String::toUpperCase)
                .filter(s -> s.length() > 12)
                .subscribe(Out::println);
    }

    private void counterFor10ValuesEach100ms() {
        var latch = new CountDownLatch(1);

        Flux.interval(Duration.ofMillis(100))
                .take(10)
                .doOnComplete(latch::countDown)
                .subscribe(Out::println);

        try {
            latch.await();
        } catch (InterruptedException e) {
            errPrintln(e.getStackTrace());
        }
    }

    private void collectCounterFor10ValuesEach100ms() {
        Flux.interval(Duration.ofMillis(100))
                .take(10)
                .collectList()
                .blockOptional()
                .ifPresent(counters -> counters.forEach(Out::println));
    }

    private void capitalizeAsync() {
        Flux.fromIterable(List.of(
                        "www.google.com",
                        "www.yahoo.com",
                        "www.altavista.com"
                ))
                .flatMap(url -> Mono.just(url)
                        .delayElement(Duration.ofMillis(120))
                        .map(String::toUpperCase))
                .collectList()
                .blockOptional()
                .ifPresent(Out::println);
    }

    private void merge() {
        var seq1 = Flux.just("seq 1.1", "seq 1.2", "seq 1.3")
                .delaySequence(Duration.ofMillis(400));
        var seq2 = Flux.just("seq 2.1", "seq 2.2", "seq 2.3", "seq 2.4")
                .delayElements(Duration.ofMillis(200));

        seq1.mergeWith(seq2)
                .collectList()
                .blockOptional()
                .ifPresent(Out::println);

    }

    private void concat() {
        var seq1 = Flux.just("seq 1.1", "seq 1.2", "seq 1.3")
                .delaySequence(Duration.ofMillis(400));
        var seq2 = Flux.just("seq 2.1", "seq 2.2", "seq 2.3", "seq 2.4")
                .delayElements(Duration.ofMillis(200));

        seq1.concatWith(seq2)
                .collectList()
                .blockOptional()
                .ifPresent(Out::println);

    }

    private void fromMonos() {
        var mono1 = Mono.just("mono1");
        var mono2 = Mono.just("mono2");

        mono1.concatWith(mono2)
                .subscribe(Out::println);
    }

    private void combineLatest() {
        Flux.combineLatest(
                Flux.just("seq 1.1", "seq 1.2", "seq 1.3"),
                Flux.just("seq 2.1", "seq 2.2", "seq 2.3", "seq 2.4"),
                (v1, v2) -> String.format("%s and %s", v1, v2)
        ).subscribe(Out::println);

    }

    private void onErrorReturn() {
        Flux.error(new IllegalStateException())
                .onErrorReturn("Fallback value if error")
                .subscribe(Out::println);

        Flux.error(new IllegalArgumentException())
                .onErrorReturn(IllegalStateException.class, "Fallback value if IllegalStateException")
                .onErrorReturn(IllegalArgumentException.class, "Fallback value if IllegalArgumentException")
                .subscribe(Out::println);

        Flux.error(new IllegalArgumentException("wrong arg"))
                .onErrorResume(e -> Flux.just("Message 1", "Message 2"))
                .subscribe(Out::println);
    }

    private void dealWithCheckedExceptions() {
        Flux.just("value")
                .map(v -> {
                    try {
                        return badNetwork(v);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .onErrorReturn("Fallback value")
                .subscribe(Out::println);
    }

    private String badNetwork(String ignored) throws IOException {
        throw new IOException("Bad network");
    }

    private void dealWithErrors() {
        println("  Error handler:");
        println("-----------------");

        Flux.range(1, 4)
                .map(i -> {
                    if (i == 4) {
                        throw new RuntimeException("4 achieved");
                    }
                    return i;
                })
                .subscribe(Out::println, Out::errPrintln, () -> println("Range completed"));

        println("  Static fallback value:");
        println("-----------------");

        Flux.just(1, 2, 3, 0)
                .map(i -> "Value: " + 60 / i)
                .onErrorReturn("RECOVERED")
                .subscribe(Out::println);

        println("  Error predicate:");
        println("-----------------");

        Flux.just(1, 2, 3, 0)
                .map(i -> "Value: " + 60 / i)
                .onErrorReturn(e -> e.getMessage().equals("/ by zero"), "Recovered for arithmetic exception")
                .subscribe(Out::println);

        println("  Fallback method:");
        println("-----------------");

        Flux.just("key1", "key2")
                .flatMap(k -> callExternalService(k)
                        .onErrorResume(e -> getFromCache(k))
                )
                .subscribe(Out::println);

        println("  Catch and rethrow:");
        println("-----------------");

        Flux.just("key1")
                .flatMap(k -> callExternalService(k))
                .onErrorResume(original -> Flux.error(new RuntimeException("Error in external service: ", original)))
                .subscribe(Out::println, Out::errPrintln);

        println("  Catch and rethrow by exception mapping:");
        println("-----------------");

        Flux.just("key1")
                .flatMap(k -> callExternalService(k))
                .onErrorMap(original -> new RuntimeException("Error in external service: ", original))
                .subscribe(Out::println, Out::errPrintln);

        println("  doFinally:");
        println("-----------------");

        Flux.just("key1")
                .flatMap(k -> callExternalService(k))
                .doFinally(signalType -> println("doFinally for " + signalType))
                .subscribe(Out::println, Out::errPrintln);

        println("  using Disposable:");
        println("-----------------");

        Flux.using(() -> new Disposable() {
                            @Override
                            public void dispose() {
                                println("I was disposed");
                            }

                            @Override
                            public String toString() {
                                return "DISPOSABLE";
                            }
                        },
                        disposable -> Flux.just(disposable.toString()),
                        Disposable::dispose
                )
                .subscribe(Out::println, Out::errPrintln);

        println("  Retry:");
        println("--------------");

        Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1)
                .elapsed()
                .subscribe(System.out::println, System.err::println);

        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        println("  Retry when:");
        println("----------------");

        Flux.error(new IllegalArgumentException("exception for retry companion"))
                .doOnError(Out::errPrintln)
                .retryWhen(Retry.from((companion) -> companion.map(retrySignal -> {
                    if (retrySignal.totalRetries() < 3) {
                        return retrySignal.totalRetries();
                    }
                    throw Exceptions.propagate(retrySignal.failure());
                })));

        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Flux<String> callExternalService(String k) {
        return Flux.error(new IllegalArgumentException("wrong arg for " + k));
    }

    private Flux<String> getFromCache(String k) {
        return Flux.just("Cached value for " + k);
    }

    private void dealWithSinks() {
        println("  replayAll Sink:");
        println("---------------");

        var replayAllSink = Sinks.many().replay().<Integer>all();
        replayAllSink.emitNext(1, FAIL_FAST);
        replayAllSink.emitNext(2, FAIL_FAST);
        replayAllSink.emitNext(3, FAIL_FAST);
        replayAllSink.emitNext(4, FAIL_FAST);
        replayAllSink.asFlux().subscribe(i -> println("replay sink value in subscriber 1 is " + i));
        replayAllSink.emitNext(5, FAIL_FAST);
        replayAllSink.asFlux().subscribe(i -> println("replay sink value in subscriber 2 is " + i));
        replayAllSink.emitNext(6, FAIL_FAST);

        println("\n  replay limited history Sink:");
        println("---------------");

        var replayLimitSink = Sinks.many().replay().<Integer>limit(2);
        replayLimitSink.emitNext(1, FAIL_FAST);
        replayLimitSink.emitNext(2, FAIL_FAST);
        replayLimitSink.emitNext(3, FAIL_FAST);
        replayLimitSink.emitNext(4, FAIL_FAST);
        replayLimitSink.asFlux().subscribe(i -> println("replay limit sink value in subscriber 1 is " + i));
        replayLimitSink.emitNext(5, FAIL_FAST);
        replayLimitSink.asFlux().subscribe(i -> println("replay limit sink value in subscriber 2 is " + i));
        replayLimitSink.emitNext(6, FAIL_FAST);

        println("\n  unicast Sink:");
        println("---------------");

        var unicastSink = Sinks.many().unicast().onBackpressureBuffer();
        unicastSink.emitNext(1, FAIL_FAST);
        unicastSink.emitNext(2, FAIL_FAST);
        unicastSink.emitNext(3, FAIL_FAST);
        unicastSink.emitNext(4, FAIL_FAST);
        unicastSink.asFlux().subscribe(i -> println("unicastSink sink value in subscriber is " + i));
        unicastSink.emitNext(5, FAIL_FAST);

        println("\n  multicast Sink:");
        println("---------------");

        var multicastSink = Sinks.many().multicast().onBackpressureBuffer();
        multicastSink.emitNext(1, FAIL_FAST);
        multicastSink.emitNext(2, FAIL_FAST);
        multicastSink.emitNext(3, FAIL_FAST);
        multicastSink.emitNext(4, FAIL_FAST);
        multicastSink.asFlux().subscribe(i -> println("multicast sink value in subscriber 1 is " + i));
        multicastSink.emitNext(5, FAIL_FAST);
        multicastSink.asFlux().subscribe(i -> println("multicast sink value in subscriber 2 is " + i));
        multicastSink.emitNext(6, FAIL_FAST);
    }

    private void zip() {
        Flux.zip(Flux.just("name1", "name2"), Flux.just("surname1", "surname2"))
                .map(t -> String.format("Name: %10s, surname: %12s", t.getT1(), t.getT2()))
                .subscribe(Out::println);
    }

    private void ignoreElements() {
        Flux.just("v1", "v2")
                .ignoreElements()
                .subscribe(Out::println);
    }

    private void fluxCompletion() {
        Flux.just("v1", "v2")
                .then()
                .subscribe(Out::println);
    }

    private void toIterable() {
        Flux.just("v1", "v2", "v3").toIterable().forEach(Out::println);
    }

    private void blockingRepositoryToFlux() {
        var latch = new CountDownLatch(1);

        var repository = new BlockingRepository();

        Flux.defer(() ->
                        Flux.fromIterable(repository.findAll())
                                .subscribeOn(Schedulers.boundedElastic()))
                .doOnComplete(latch::countDown)
                .subscribe(Out::println);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeBlockingCall() {
        Mono.fromCallable(() -> new BlockingRepository().findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(Out::println);

        try {
            Thread.sleep(Duration.ofSeconds(2).toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fluxToBlockingRepository() {
        var repository = new BlockingRepository();

        Flux.just("dataToSave1", "dataToSave2")
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(repository::save)
                .then()   // <-- ignore results
                .block(); // <-- just to wait synchronously for the end to see results
    }

    private static final class BlockingRepository {

        Iterable<String> findAll() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return Arrays.asList("name1", "name2", "name3");
        }

        void save(String data) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            println(String.format("Data %s is saved", data));
        }
    }

    private void exponentialBackoff() {
        AtomicInteger errorCount = new AtomicInteger();

        Flux.<String>error(new IllegalStateException("boom"))
                .doOnError(e -> {
                    errorCount.incrementAndGet();
                    println(e + " at " + LocalTime.now());
                })
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(100)).jitter(0d)
                                .doAfterRetry(rs -> println("retried at " + LocalTime.now()))
                                .onRetryExhaustedThrow((spec, rs) -> rs.failure())
                )
                .subscribe(Out::println);

        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void useDisposable() {
        var disposable = Flux.interval(Duration.ofMillis(400))
                .subscribe(Out::println);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        disposable.dispose();
        println("isDisposed:" + disposable.isDisposed());
    }

    private void batchSubscriber() {
        Flux.range(1, 5)
                .subscribe(new Subscriber<>() {

                    private volatile Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(2);
                    }

                    @Override
                    public void onNext(Integer i) {
                        println(i);
                        if (i == 2) {
                            subscription.request(2);
                        } else if (i == 4) {
                            subscription.request(3);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        errPrintln(t);
                    }

                    @Override
                    public void onComplete() {
                        println("Subscriber completed");
                    }
                });
    }

    private void simpleSubscriber() {
        Flux.range(1, 5)
                .subscribe(new BaseSubscriber<Integer>() {

                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        println("Subscribed");
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        println(value);
                        request(1);
                    }

                    @Override
                    protected void hookOnComplete() {
                        println("Subscriber completed");
                    }

                });
    }

    private void cancelSubscriber() {
        Flux.range(1, 10)
                .doOnRequest((r) -> println("Requested of " + r))
                .subscribe(new BaseSubscriber<>() {

                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        println(value);
                        cancel();
                    }

                    @Override
                    protected void hookOnCancel() {
                        println("Cancelled");
                    }
                });
    }

    private void limitRate() {
        Flux.range(1, 10)
                .doOnRequest((r) -> println("Requested of " + r))
                .limitRate(3)
                .subscribe(Out::println);
    }

    private void limitRequest() {
        Flux.range(1, 10)
                .doOnRequest((r) -> println("Requested of " + r))
                .take(4, false)
                .subscribe(Out::println);

        Flux.range(1, 10)
                .doOnRequest((r) -> println("Requested of " + r))
                .take(4, true)
                .subscribe(Out::println);
    }

    private void syncGenerate() {
        Flux.generate(() -> 0, (state, sink) -> {
            sink.next("state=" + state);

            if (state == 12) {
                sink.complete();
            }
            return state + 1;
        }).subscribe(Out::println);
    }

    private void createWithDropStrategy() {
        var latch = new CountDownLatch(1);

        Flux.create(emitter -> {
                    for (int i = 0; i <= 300; i++) {
                        println(Thread.currentThread().getName() + " | Publishing = " + i);
                        emitter.next(i);
                    }
                    emitter.complete();
                }, FluxSink.OverflowStrategy.DROP)
                .onBackpressureDrop((i) -> println(Thread.currentThread().getName() + " | DROPPED!! = " + i))
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(latch::countDown)
                .subscribe(i -> {
                            println(Thread.currentThread().getName() + " | Received   = " + i);

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        },
                        e -> println(Thread.currentThread().getName() + " | ERROR!! = " + e.getMessage()));

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createWithLatestStrategy() {
        var latch = new CountDownLatch(1);

        Flux.create(emitter -> {
                    for (int i = 0; i <= 300; i++) {
                        println(Thread.currentThread().getName() + " | Publishing = " + i);
                        emitter.next(i);

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    emitter.complete();
                }, FluxSink.OverflowStrategy.LATEST)
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(latch::countDown)
                .subscribe((i) -> {
                    println(Thread.currentThread().getName() + " | Received   = " + i);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createWithErrorStrategy() {
        Flux.create(emitter -> {
                    for (int i = 0; i <= 300; i++) {
                        println(Thread.currentThread().getName() + " | Publishing = " + i);
                        emitter.next(i);
                    }
                    emitter.complete();
                }, FluxSink.OverflowStrategy.ERROR)
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        i -> println(Thread.currentThread().getName() + " | Received   = " + i),
                        e -> println(Thread.currentThread().getName() + " | ERROR   = " + e.getMessage())
                );

        try {
            TimeUnit.SECONDS.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createWithIgnoreStrategy() {
        Flux.create(emitter -> {
                    for (int i = 0; i <= 1000; i++) {
                        println(Thread.currentThread().getName() + " | Publishing = " + i);
                        emitter.next(i);
                    }
                    emitter.complete();
                }, FluxSink.OverflowStrategy.IGNORE)
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        i -> println(Thread.currentThread().getName() + " | Received   = " + i),
                        e -> println(Thread.currentThread().getName() + " | ERROR   = " + e.getMessage())
                );

        try {
            TimeUnit.SECONDS.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createWithBufferStrategy() {
        Flux.create(emitter -> {
                    for (int i = 0; i <= 1000; i++) {
                        println(Thread.currentThread().getName() + " | Publishing = " + i);
                        emitter.next(i);
                    }
                    emitter.complete();
                }, FluxSink.OverflowStrategy.BUFFER)
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        i -> {
                            println(Thread.currentThread().getName() + " | Received   = " + i);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        },
                        e -> println(Thread.currentThread().getName() + " | ERROR   = " + e.getMessage())
                );

        try {
            TimeUnit.SECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hybridPushPull() {
        Flux.create(sink -> {
                    sink.onRequest(n -> {
                        println("Requested " + n);

                        for (int i = 0; i < n; i++) {
                            println("Producing " + i);
                            sink.next(i);
                        }

                        sink.complete();
                    });
                })
                .take(10, true)
                .subscribe(Out::println);

    }

    private void handle() {
        Flux.just(-1, 30, 13, -2, 9, 20)
                .handle((i, sink) -> {
                    if (i >= 0) {
                        sink.next(i);
                    }
                }).subscribe(Out::println);
    }

    private void publishOnParallelScheduler() {
        var latch = new CountDownLatch(1);

        var scheduler = Schedulers.newParallel("parallel-scheduler", 4);
        Flux.range(1, 4)
                .map(i -> {
                    println("Multiply map in thread " + Thread.currentThread().getName());
                    return i * 10;
                })
                .publishOn(scheduler)
                .map(i -> {
                    println("Stringify map in thread " + Thread.currentThread().getName());
                    return "Value : " + i;
                })
                .subscribe(i -> println("Got " + i + " in thread " + Thread.currentThread().getName()));

        try {
            latch.await(25, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scheduler.dispose();
        }
    }

    private void publishOnSingleScheduler() {
        var latch = new CountDownLatch(1);

        var scheduler = Schedulers.single();
        Flux.range(1, 4)
                .map(i -> {
                    println("Multiply map in thread " + Thread.currentThread().getName());
                    return i * 10;
                })
                .publishOn(scheduler)
                .map(i -> {
                    println("Stringify map in thread " + Thread.currentThread().getName());
                    return "Value : " + i;
                })
                .subscribe(i -> println("Got " + i + " in thread " + Thread.currentThread().getName()));

        try {
            latch.await(25, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scheduler.dispose();
        }
    }

    private void subscribeOnParallelScheduler() {
        var latch = new CountDownLatch(1);

        var scheduler = Schedulers.newParallel("parallel-scheduler", 4);
        Flux.range(1, 4)
                .map(i -> {
                    println("Multiply map in thread " + Thread.currentThread().getName());
                    return i * 10;
                })
                .subscribeOn(scheduler)
                .map(i -> {
                    println("Stringify map in thread " + Thread.currentThread().getName());
                    return "Value : " + i;
                })
                .subscribe(i -> println("Got " + i + " in thread " + Thread.currentThread().getName()));

        try {
            latch.await(25, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scheduler.dispose();
        }
    }

    private void transform() {
        Function<Flux<String>, Flux<String>> filterAndMap =
                (f) -> f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);

        Flux.just("blue", "green", "orange", "purple")
                .transform(filterAndMap)
                .subscribe(v -> println("Value in subscribe: " + v));
    }

    private void transformDeferred() {
        var ai = new AtomicInteger();

        Function<Flux<String>, Flux<String>> filterAndMap = v ->
                ai.incrementAndGet() == 1 ?
                        v.filter(color -> !color.equals("orange")).map(String::toUpperCase) :
                        v.filter(color -> !color.equals("purple")).map(String::toUpperCase);

        var flux = Flux.just("blue", "green", "orange", "purple")
                .transformDeferred(filterAndMap);

        flux.subscribe(v -> println("Value in subscriber 1 : " + v));
        flux.subscribe(v -> println("Value in subscriber 2 : " + v));
    }

    private void hotPublisherJust() {
        var numbersFlux = Flux.just(1, 2, 3);

        numbersFlux.subscribe(n -> println("Value in subscriber1 : " + n));
        numbersFlux.subscribe(n -> println("Value in subscriber2 : " + n));
    }

    private void hotPublisherUsingSinks() {
        var hotSource = Sinks.unsafe().many().multicast().<String>directBestEffort();
        var hotFlux = hotSource.asFlux().map(String::toUpperCase);

        hotFlux.subscribe(d -> println("Subscriber 1 to Hot Source: " + d));

        hotSource.emitNext("blue", FAIL_FAST);
        hotSource.tryEmitNext("green").orThrow();

        hotFlux.subscribe(d -> println("Subscriber 2 to Hot Source: " + d));

        hotSource.emitNext("orange", FAIL_FAST);
        hotSource.emitNext("purple", FAIL_FAST);
        hotSource.emitComplete(FAIL_FAST);
    }

    private void coldPublisher() {
        var rnd = new Random();

        var deferred = Flux.defer(() -> {
            println("make a new data sequence");

            var i = rnd.nextInt(4);

            return Flux.just("a" + i, "b" + i, "c" + i);
        });

        deferred.subscribe(n -> println("Value in subscriber1 : " + n));
        deferred.subscribe(n -> println("Value in subscriber2 : " + n));
    }

    private void connectableFlux() {
        var rnd = new Random();

        var source = Flux.defer(() -> {
                    println("make a new data sequence");

                    var i = rnd.nextInt(4);

                    return Flux.just("a" + i, "b" + i, "c" + i);
                })
                .doOnSubscribe(s -> println("subscribed to source"));

        var connectable = source.publish();

        connectable.subscribe(n -> println("Value in subscriber1 : " + n));
        connectable.subscribe(n -> println("Value in subscriber2 : " + n));

        println("done subscribing");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        println("will now connect");

        connectable.connect();
    }

    private void autoConnect() {
        var rnd = new Random();

        var source = Flux.defer(() -> {
                    println("make a new data sequence");

                    var i = rnd.nextInt(4);

                    return Flux.just("a" + i, "b" + i, "c" + i);
                })
                .doOnSubscribe(s -> println("subscribed to source"));

        var autoConnect = source.publish().autoConnect(2);

        autoConnect.subscribe(n -> println("Value in subscriber1 : " + n));
        println("subscribed first");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        println("subscribing second");
        autoConnect.subscribe(n -> println("Value in subscriber2 : " + n));
    }

    private void groupBy() {
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even" : "odd")
                .concatMap(
                        g -> g.defaultIfEmpty(-1)
                                .map(String::valueOf)
                                .startWith(g.key())
                )
                .subscribe(Out::println);

    }

    private void windows() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .window(5, 3) //overlapping windows
                .concatMap(g -> g.defaultIfEmpty(-1))
                .subscribe(Out::println);
    }

    private void buffer() {
        Flux.range(1, 10)
                .buffer(5, 3)
                .subscribe(Out::println);
    }

    private void bufferWhile() {
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .bufferWhile(i -> i % 2 == 0)
                .subscribe(Out::println);
    }

    private void useParallel() {
        Flux.range(1, 10)
                .parallel(2)
                .subscribe(i -> println(Thread.currentThread().getName() + " -> " + i));
    }

    private void replaceSchedulers() {
        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
                .subscribe(i -> println(Thread.currentThread().getName() + " -> " + i));

        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
