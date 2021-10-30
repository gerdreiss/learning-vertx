package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  companion object {
    private val LOG: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {

    val router = Router.router(vertx)
    router.route().failureHandler(::failureHandler)
    Routes.root(router)
    Routes.assets(router)
    Routes.asset(router)
    Routes.quotes(router)

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

  private fun failureHandler(errorContext: RoutingContext) {
    if (errorContext.response().ended()) {
      // ignore
    } else {
      LOG.error("Route error: ${errorContext.failure()}")
      errorContext.response()
        .setStatusCode(500)
        .end(
          JsonObject()
            .put("message", "Something went wrong")
            .toBuffer()
        )
    }
  }
}

private val LOG: Logger = LoggerFactory.getLogger("main")

fun main() {
  val vertx = Vertx.vertx()
  vertx.exceptionHandler { LOG.error("Unhandled: $it") }
  vertx.deployVerticle(MainVerticle()) {
    if (it.failed()) {
      LOG.error("Failed to deploy: ${it.cause()}")
    } else {
      LOG.info("Deployed verticle: ${MainVerticle::class.java.simpleName}")
    }
  }
}
