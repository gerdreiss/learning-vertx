package vertx.mutiny.playground

import io.vertx.mutiny.core.Vertx
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("mutiny.main")

fun main() {
  Vertx.vertx()
    .deployVerticle(VertxMutinyWeb())
    .subscribe().with { id -> logger.info("Started $id") }
}
