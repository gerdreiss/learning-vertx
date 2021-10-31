package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  companion object {
    private val LOG: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {
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
      .exceptionHandler { LOG.error("HTTP server error: $it") }
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          LOG.info("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}
