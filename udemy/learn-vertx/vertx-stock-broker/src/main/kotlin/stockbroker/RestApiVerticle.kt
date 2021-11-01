package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RestApiVerticle : AbstractVerticle() {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(RestApiVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {

    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { config ->
        logger.info("Config loaded: $config")

        val router = Router.router(vertx)
        router.route()
          .handler(BodyHandler.create())
          .failureHandler(FailureHandler())

        Routes.root(router)
        Routes.assets(router)
        Routes.asset(router)
        Routes.quotes(router)
        Routes.getWatchlist(router)
        Routes.postWatchlist(router)
        Routes.deleteWatchlist(router)

        vertx
          .createHttpServer()
          .requestHandler(router)
          .exceptionHandler { logger.error("HTTP server error: $it") }
          .listen(config.serverPort) { http ->
            if (http.succeeded()) {
              startPromise.complete()
              logger.info("HTTP server started on port ${config.serverPort}")
            } else {
              startPromise.fail(http.cause());
            }
          }
      }
  }
}