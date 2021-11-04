package vertx.mutiny.playground

import io.smallrye.mutiny.Multi
import org.slf4j.LoggerFactory
import java.util.stream.Stream
import kotlin.random.Random

private val logger = LoggerFactory.getLogger("hello.multi")

fun main() {
  val items = Stream
    .iterate(1000) { it - Random.nextInt(11) }
  Multi.createFrom().items(items)
    .onItem().transform { item -> item - Random.nextInt(11) }
    .onItem().transform { item -> item / Random.nextInt(3) }
    .onFailure().recoverWithItem { error ->
      logger.error("transformation failed with $error")
      0
    }
    .onItem().transform { item -> item.toString() }
    .select().first(400)
    .select().last(10)
    .subscribe().with(
      { item -> logger.info("Item: $item") },
      { failure -> logger.error("Failed with $failure") }
    )
}
