package stockbroker

import io.vertx.core.Vertx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
