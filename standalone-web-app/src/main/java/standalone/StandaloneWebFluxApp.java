package standalone;

import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.DisposableChannel;
import reactor.netty.http.server.HttpServer;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class StandaloneWebFluxApp {

  public static void main(String[] args) {
    var httpHandler = RouterFunctions.toHttpHandler(appRoutes());

    HttpServer.create()
        .port(8080)
        .handle(new ReactorHttpHandlerAdapter(httpHandler))
        .bind()
        .flatMap(DisposableChannel::onDispose)
        .block();

  }

  private static RouterFunction<ServerResponse> appRoutes() {
    return route(GET("/hello").and(accept(TEXT_PLAIN)), Handler::hello)
        .andRoute(GET("/users/{userId}").and(accept(APPLICATION_JSON)), Handler::getUserById);
  }
}
