package stockbroker


import arrow.core.flatMap
import arrow.core.getOrHandle
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Routes {

  private val LOG: Logger = LoggerFactory.getLogger(Routes::class.java)

  private val persistentStore = MemStore
  private val assetService = AssetService(persistentStore)
  private val quoteService = QuoteService(persistentStore)

  fun root(parent: Router): Route =
    parent.get("/").handler { context ->
      context.response()
        .putHeader("Content-Type", "text/html; charset=UTF-8")
        .end("<center><h1>hello!</h1></center>")
    }

  fun assets(parent: Router): Route =
    parent.get("/assets").handler { context ->
      val assets = assetService.getAll()
      LOG.info("Path ${context.normalizedPath()} responds with\n$assets")
      val response = assets.fold(JsonArray()) { jsonArray, asset ->
        jsonArray.add(asset.toJson())
      }
      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer())
    }

  fun asset(parent: Router): Route =
    parent.get("/assets/:symbol")
      .handler { context ->
        assetService.getBySymbol(context.pathParam("symbol"))
          .map { asset ->
            val response = asset.toJson()
            LOG.debug("Path ${context.normalizedPath()} responds with\n$response")
            context.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(response.toBuffer())
          }
          .getOrHandle { notFound(context, it) }
      }

  fun quotes(parent: Router): Route =
    parent.get("/assets/:symbol/quotes")
      .handler { context ->
        context.pathParam("symbol")?.let { symbol ->
          LOG.debug("asset param: $symbol")
          assetService.getBySymbol(symbol)
            .flatMap(quoteService::getForAsset)
            .map { quote ->
              val response = quote.toJson()
              LOG.debug("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
              context.response()
                .putHeader("Content-Type", "application/json")
                .end(response.toBuffer())
            }
            .getOrHandle { notFound(context, it) }
        }
      }

  private fun notFound(context: RoutingContext, message: String): Future<Void> {
    LOG.error(message)
    return context.response()
      .setStatusCode(404)
      .setStatusMessage(message)
      .end()
  }
}

