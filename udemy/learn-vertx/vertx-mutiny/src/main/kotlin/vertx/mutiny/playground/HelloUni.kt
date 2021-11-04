package vertx.mutiny.playground

import io.smallrye.mutiny.Uni
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("hello.uni")

fun main() {
  // uni represents a stream that can only emit either an item or a failure
  Uni.createFrom().item("Hello")
    .onItem().transform { item -> "$item Mutiny!" }
    .onItem().transform { item -> item.uppercase() }
    .subscribe()
    .with { item ->
      logger.info("Item: $item")
    }

  Uni.createFrom().item("Ignored due to failure")
    .onItem().castTo(Integer::class.java)
    .subscribe()
    .with(
      { item -> logger.info("Item: $item") },
      { failure -> logger.error("Failed with $failure") }
    )
}
