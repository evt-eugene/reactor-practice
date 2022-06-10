package practice;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static practice.Out.println;

public class StepVerifierTests {

    @Test
    public void testFooBar() {
        var source = Flux.just("foo", "bar");

        StepVerifier.create(source)
                .expectSubscription()
                .expectNext("foo")
                .expectNext("bar")
                .expectComplete()
                .verify();
    }

    @Test
    public void testRange() {
        var source = Flux.range(0, 100);

        StepVerifier.create(source)
                .expectSubscription()
                .expectNext(0)
                .expectNextCount(98)
                .expectNext(99)
                .expectComplete()
                .verify();
    }

    @Test
    public void testAppendBoomError() {
        var source = Flux.just("thing1", "thing2")
                .concatWith(Mono.error(new IllegalArgumentException("boom")));

        StepVerifier.create(source)
                .expectNext("thing1")
                .expectNext("thing2")
                .expectErrorMessage("boom")
                .verify();
    }

    @Test
    public void testMatches() {
        var source = Flux.just("alpha-foo", "beta-bar");

        StepVerifier.create(source)
                .expectSubscription()
                .expectNextMatches(e -> e.startsWith("alpha"))
                .expectNextMatches(e -> e.startsWith("beta"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testWallet() {
        var source = Mono.just(new Wallet("admin", "USD"));

        StepVerifier.create(source)
                .expectSubscription()
                .assertNext(wallet -> assertThat(wallet, hasProperty("currency", equalTo("USD"))))
                .expectComplete()
                .verify();
    }

    @Test
    public void testWallets() {
        var source = Flux.just(new Wallet("admin1", "USD"), new Wallet("admin2", "USD"));

        StepVerifier.create(source)
                .expectSubscription()
                .recordWith(ArrayList::new) // or ConcurrentLinkedQueue::new
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(wallets -> {
                    assertThat(wallets, hasSize(2));
                    assertThat(wallets, everyItem(hasProperty("owner", startsWith("admin"))));
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testEachWallet() {
        var source = Flux.just(new Wallet("admin1", "USD"), new Wallet("admin2", "USD"));

        StepVerifier.create(source)
                .expectSubscription()
                .thenConsumeWhile(x -> true, wallet -> {
                    assertThat(wallet, hasProperty("owner", startsWith("admin")));
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testError() {
        var source = Flux.error(new IllegalArgumentException("arg is incorrect"));

        StepVerifier.create(source)
                .expectError()
                .verify();
    }

    @Test
    public void testSpecificError() {
        var source = Flux.error(new IllegalArgumentException("arg is incorrect"));

        StepVerifier.create(source)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void testInfiniteStream() {
        var source = Flux.interval(Duration.ofMillis(25));

        StepVerifier.create(source)
                .expectSubscription()
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .thenCancel()
                .verify();
    }

    @Test
    public void testBackpressure() {
        var source = Flux.generate(() -> 0, (state, sink) -> {
            if (state == 0) {
                sink.next("Connected");
            } else if (state == 1) {
                sink.next("Price: 12$");
            } else if (state == 2) {
                sink.error(Exceptions.failWithOverflow());
            }
            return state + 1;
        });

        StepVerifier.create(source.onBackpressureBuffer(5), 0) // initial request = 0
                .expectSubscription()
                .thenRequest(1)
                .expectNext("Connected")
                .thenRequest(1)
                .expectNext("Price: 12$")
                .expectError(Exceptions.failWithOverflow().getClass())
                .verify();
    }

    @Test
    public void testPublisher() {
        var idsPublisher = TestPublisher.<String>create();

        StepVerifier.create(new WalletsRepository().findAllById(idsPublisher))
                .expectSubscription()
                .then(() -> idsPublisher.next("1"))
                .assertNext(w -> assertThat(w, hasProperty("owner", equalTo("admin1"))))
                .then(() -> idsPublisher.next("2"))
                .assertNext(w -> assertThat(w, hasProperty("owner", equalTo("admin2"))))
                .then(idsPublisher::complete)
                .expectComplete()
                .verify();
    }

    private static class WalletsRepository {

        Publisher<Wallet> findAllById(Publisher<String> ids) {
            return Flux.from(ids).map(id -> new Wallet("admin" + id, "USD"));
        }
    }

    @Test
    public void testVirtualTimeUsingVirtualTimeScheduler() {
        StepVerifier.withVirtualTime(() ->
                        Flux.interval(Duration.ofMinutes(1))
                                .zipWith(Flux.just("a", "b", "c"))
                                .map(Tuple2::getT2)
                )
                .expectSubscription()
                .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofMinutes(3)))
                .expectNext("a", "b", "c")
                .expectComplete()
                .verify();
    }

    @Test
    public void testVirtualTimeUsingAwait() {
        StepVerifier.withVirtualTime(() ->
                        Flux.interval(Duration.ofMinutes(1))
                                .zipWith(Flux.just("a", "b", "c"))
                                .map(Tuple2::getT2)
                )
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(3))
                .expectNext("a", "b", "c")
                .expectComplete()
                .verify();
    }

    @Test
    public void testTimings() {
        StepVerifier.withVirtualTime(() ->
                        Flux.interval(Duration.ofSeconds(1)).take(3)
                )
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .expectNext(0L)
                .expectNoEvent(Duration.ofSeconds(1))
                .expectNext(1L)
                .expectNoEvent(Duration.ofSeconds(1))
                .expectNext(2L)
                .expectComplete()
                .verify();
    }

    @Test
    public void testContext() {
        var source = new SecurityService().login("admin", "pswd");

        StepVerifier.create(source)
                .expectSubscription()
                .expectAccessibleContext()
                .contains("username", "admin")
                .contains("password", "pswd")
                .then()
                .verifyComplete();
    }

    private static class SecurityService {

        Publisher<String> login(String username, String password) {
            return Flux.<String>empty().contextWrite(ctx -> ctx.put("username", username).put("password", password));
        }
    }

    @Test
    public void testDeferredContext() {
        var source = Mono.just("Hello")
                .flatMap(str -> Mono.deferContextual(ctx -> Mono.just(str + " " + ctx.get("message"))))
                .contextWrite(ctx -> ctx.put("message", "World"));

        StepVerifier.create(source)
                .expectSubscription()
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    public void testThatHasNoDiscardedElementsAndIsFasterThanOneMinute() {
        var source = Flux.just("a", "b", "c");

        StepVerifier.create(source)
                .expectSubscription()
                .expectNext("a")
                .expectNext("b")
                .expectNext("c")
                .expectComplete()
                .verifyThenAssertThat()
                .hasNotDiscardedElements()
                .tookLessThan(Duration.ofMinutes(1));
    }

    @Test
    public void testSplitPathIsUsed() {
        var source = splitOrFallback(Mono.just("just a  phrase with    tabs!"), Mono.just("EMPTY_PHRASE"));

        StepVerifier.create(source)
                .expectNext("just", "a", "phrase", "with", "tabs!")
                .verifyComplete();
    }

    @Test
    public void testEmptyPathIsUsed() {
        var source = splitOrFallback(Mono.empty(), Mono.just("EMPTY_PHRASE"));

        StepVerifier.create(source)
                .expectNext("EMPTY_PHRASE")
                .verifyComplete();
    }

    private Flux<String> splitOrFallback(Mono<String> source, Publisher<String> fallback) {
        return source
                .flatMapMany(phrase -> Flux.fromArray(phrase.split("\\s+")))
                .switchIfEmpty(fallback);
    }

    @Test
    public void testCommandEmptyPathIsUsedUsingPublisherProbe() {
        PublisherProbe<Void> probe = PublisherProbe.empty();
        var source = processOrFallback(Mono.empty(), probe.mono());

        StepVerifier.create(source)
                .verifyComplete();

        probe.assertWasSubscribed();
        probe.assertWasRequested();
        probe.assertWasNotCancelled();
    }

    public Mono<Void> processOrFallback(Mono<String> commandSource, Mono<Void> doWhenEmpty) {
        return commandSource
                .flatMap(command -> Mono.just(command + " DONE").then())
                .switchIfEmpty(doWhenEmpty);
    }

    @Test
    public void testTraceback() {
        Hooks.onOperatorDebug();

        Flux.just(0, 1)
                .single()
                .subscribe(Out::println);
    }

    @Test
    public void testCheckpoint() {
        Flux.just(0, 1)
                .single()
                .checkpoint("Checkpoint1")
                .subscribe(Out::println);
    }

    @Test
    public void testLog() {
        Flux.just(0, 1, 2, 3, 4, 5)
                .log()
                .take(3)
                .subscribe(i -> println("Got " + i + " in subscribe"));
    }

    @Test
    public void testGroupBy() {
        var source = Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even" : "odd")
                .concatMap(g -> g.defaultIfEmpty(-1) // if empty groups, show them
                        .map(String::valueOf) // map to string
                        .startWith(g.key())   // start with the group's key
                );
        StepVerifier.create(source)
                .expectNext("odd", "1", "3", "5", "11", "13")
                .expectNext("even", "2", "4", "6", "12")
                .verifyComplete();
    }

    @Test
    public void testWindowsOverlapping() {
        var source = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .window(5, 3) //overlapping windows
                .concatMap(g -> g.defaultIfEmpty(-1)); //show empty windows as -1

        StepVerifier.create(source)
                .expectNext(1, 2, 3, 4, 5)
                .expectNext(4, 5, 6, 7, 8)
                .expectNext(7, 8, 9, 10)
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    public void testWindowWhile() {
        var source = Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .windowWhile(i -> i % 2 == 0)
                .concatMap(g -> g.defaultIfEmpty(-1));

        StepVerifier.create(source)
                .expectNext(-1, -1, -1) //respectively triggered by odd 1 3 5
                .expectNext(2, 4, 6) // triggered by 11
                .expectNext(12) // triggered by 13
                // however, no empty completion window is emitted (would contain extra matching elements)
                .verifyComplete();
    }

    @Test
    public void testBuffer() {
        var source = Flux.range(1, 10)
                .buffer(5, 3); // overlapping buffers

        StepVerifier.create(source)
                .expectNext(Arrays.asList(1, 2, 3, 4, 5))
                .expectNext(Arrays.asList(4, 5, 6, 7, 8))
                .expectNext(Arrays.asList(7, 8, 9, 10))
                .expectNext(Collections.singletonList(10))
                .verifyComplete();
    }

    @Test
    public void testBufferWhile() {
        var source = Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .bufferWhile(i -> i % 2 == 0);

        StepVerifier.create(source)
                .expectNext(Arrays.asList(2, 4, 6)) // triggered by 11
                .expectNext(Collections.singletonList(12)) // triggered by 13
                .verifyComplete();
    }

    @Test
    public void testContextGetOrDefault() {
        var source = Mono.just("Hello")
                .contextWrite(ctx -> ctx.put("message", "World"))
                .flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.getOrDefault("message", "Stranger"))));

        StepVerifier.create(source)
                .expectNext("Hello Stranger")
                .verifyComplete();
    }

    @Test
    public void testContextWrite() {
        var source = Mono
                .deferContextual(ctx -> Mono.just("Hello " + ctx.get("message")))
                .contextWrite(ctx -> ctx.put("message", "Reactor"))
                .contextWrite(ctx -> ctx.put("message", "World"));

        StepVerifier.create(source)
                .expectNext("Hello Reactor")
                .verifyComplete();
    }

    @Test
    public void testContextWritesOrder() {
        var source = Mono
                .deferContextual(ctx -> Mono.just("Hello " + ctx.get("message")))                        // 3
                .contextWrite(ctx -> ctx.put("message", "Reactor"))                                      // 2
                .flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get("message"))))     // 4
                .contextWrite(ctx -> ctx.put("message", "World"));                                       // 1

        StepVerifier.create(source)
                .expectNext("Hello Reactor World")                                                       // 5
                .verifyComplete();                                                                       // 6
    }

    @Test
    public void testContextWithMultipleDeferContextual() {
        var source = Mono.just("Hello")
                .flatMap(s -> Mono.deferContextual(ctxView -> Mono.just(s + " " + ctxView.get("message"))))
                .flatMap(s -> Mono.deferContextual(ctxView -> Mono.just(s + " " + ctxView.get("message")))
                        .contextWrite(ctx -> ctx.put("message", "Reactor"))
                )
                .contextWrite(ctx -> ctx.put("message", "World"));

        StepVerifier.create(source)
                .expectNext("Hello World Reactor")
                .verifyComplete();
    }

    @Test
    public void contextForLibraryReactivePut() {
        Mono<String> put = doPut("www.example.com", Mono.just("Walter"))
                .contextWrite(Context.of("reactive.http.library.correlationId", "2-j3r9afaf92j-afkaf"))
                .filter(t -> t.getT1() < 300)
                .map(Tuple2::getT2);

        StepVerifier.create(put)
                .expectNext("PUT <Walter> sent to www.example.com" +
                        " with header X-Correlation-ID = 2-j3r9afaf92j-afkaf")
                .verifyComplete();
    }

    private Mono<Tuple2<Integer, String>> doPut(String url, Mono<String> data) {
        var dataAndContext = data.zipWith(Mono.deferContextual(c -> Mono.just(c.getOrEmpty("reactive.http.library.correlationId"))));

        return dataAndContext.<String>handle((dac, sink) -> {
                    if (dac.getT2().isPresent()) {
                        sink.next("PUT <" + dac.getT1() + "> sent to " + url + " with header X-Correlation-ID = " + dac.getT2().get());
                    } else {
                        sink.next("PUT <" + dac.getT1() + "> sent to " + url);
                    }
                    sink.complete();
                })
                .map(msg -> Tuples.of(200, msg));
    }
}
