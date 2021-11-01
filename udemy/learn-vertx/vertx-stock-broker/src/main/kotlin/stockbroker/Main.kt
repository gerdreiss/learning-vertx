package stockbroker

import io.vertx.core.Vertx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("main")

fun main() {
  Vertx.vertx()
    .exceptionHandler { logger.error("Unhandled: $it") }
    .deployVerticle(MainVerticle())
    .onFailure { logger.error("Failed to deploy MainVerticle: $it") }
    .onSuccess {
      logger.info("Deployed {} with id {}", RestApiVerticle::class.java.simpleName, it)
    }
}
