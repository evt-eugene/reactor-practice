package practice;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static practice.Out.errPrintln;
import static practice.Out.println;

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

        println("\nfluxToBlockingRepository:");
        fluxToBlockingRepository();

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
}
