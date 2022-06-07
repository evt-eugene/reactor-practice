package practice;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static practice.Out.println;

public class MonoCore {

    public void doPractice() {
        println("\nemptyMono:");
        emptyMono();

        println("\ndefaultIfEmpty:");
        defaultIfEmpty();

        println("\nmonoNeverSignal:");
        monoNeverSignal();

        println("\nmonoFooValue:");
        monoFooValue();

        println("\nmonoToValueWithBlock:");
        monoToValueWithBlock();

        println("\nerrorMono:");
        errorMono();

        println("\ncapitalizeWithDelay:");
        capitalizeWithDelay();

        println("\ncombineMappedAndDelay:");
        combineMappedAndDelay();

        println("\ntoCompletableFuture:");
        toCompletableFuture();

        println("\nfromCompletableFuture:");
        fromCompletableFuture();

        println("\nmonoInDifferentThread:");
        monoInDifferentThread();

        println("\nmono practice finished:");
    }

    private void emptyMono() {
        Mono.empty()
                .subscribe(Out::println);
    }

    private void defaultIfEmpty() {
        Mono.justOrEmpty((String) null)
                .defaultIfEmpty("DEFAULT")
                .subscribe(Out::println);
    }

    private void monoNeverSignal() {
        Mono.never().subscribe(Out::println);
    }

    private void monoFooValue() {
        Mono.just("foo").subscribe(Out::println);
    }

    private void monoToValueWithBlock() {
        var result = Mono.just("foo").block();
        println(result);
    }

    private void errorMono() {
        Mono.error(new IllegalStateException())
                .subscribe(Out::println, Out::errPrintln);
    }

    private void capitalizeWithDelay() {
        var result = Mono.just("hello, world")
                .delayElement(Duration.ofMillis(2000))
                .map(String::toUpperCase)
                .block();

        println(result);
    }

    private void combineMappedAndDelay() {
        Mono.firstWithValue(
                Mono.just(1).map(integer -> "foo" + integer),
                Mono.delay(Duration.ofMillis(100)).thenReturn("bar")
        ).subscribe(Out::println);
    }

    private void toCompletableFuture() {
        CompletableFuture<String> future = Mono.just("foo")
                .toFuture()
                .thenApply(s -> "!!" + s)
                .thenApply(s -> s + "!!")
                .thenCombine(CompletableFuture.supplyAsync(() -> " bar"), (s1, s2) -> s1 + s2)
                .thenApply(String::toUpperCase);

        try {
            println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fromCompletableFuture() {
        var future = CompletableFuture.completedFuture("fooBar");
        Mono.fromFuture(future).subscribe(Out::println);
    }

    private void monoInDifferentThread() {
        final var mono = Mono.just("hello, world");

        var helperThread = new Thread(() -> {
            mono
                    .map(v -> v + ", in mapping thread " + Thread.currentThread().getName())
                    .subscribe(v -> println("Got [" + v + "] in subscription thread " + Thread.currentThread().getName()));
        }, "customHelperThread");
        helperThread.start();

        try {
            helperThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
