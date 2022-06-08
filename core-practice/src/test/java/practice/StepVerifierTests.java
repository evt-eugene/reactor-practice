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
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;

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
}
