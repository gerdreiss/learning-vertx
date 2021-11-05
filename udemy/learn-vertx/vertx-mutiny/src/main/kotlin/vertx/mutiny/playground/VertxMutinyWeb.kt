package vertx.mutiny.playground

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.obj
import io.vertx.mutiny.ext.web.Router
import io.vertx.mutiny.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class VertxMutinyWeb : AbstractVerticle() {
  companion object {
    private val logger = LoggerFactory.getLogger(VertxMutinyWeb::class.java)
  }

  override fun asyncStart(): Uni<Void> {

    val router = Router.router(vertx)
    router.route().failureHandler(this::failureHandler)
    router.get("/users").respond(this::getUsers)

    return vertx.createHttpServer()
      .requestHandler(router)
      //.requestHandler { req ->
      //  logger.info("Requested ${req.absoluteURI()} with headers ${req.headers()}")
      //  req.response().endAndForget("Hello!")
      //}
      .listen(9001)
      .replaceWithVoid()
  }

  private fun failureHandler(context: RoutingContext) {
    context.response().setStatusCode(500).endAndForget("Boom!")
  }

  private fun getUsers(context: RoutingContext): Uni<JsonArray>? =
    Uni.createFrom().item(
      json {
        jsonArrayOf(
          obj("name" to "Alice"),
          obj("name" to "Bob")
        )
      }
    )
}
