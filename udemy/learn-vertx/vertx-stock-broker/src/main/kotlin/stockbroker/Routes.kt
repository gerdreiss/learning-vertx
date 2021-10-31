package stockbroker


import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrHandle
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object Routes {

  private val logger: Logger = LoggerFactory.getLogger(Routes::class.java)

  private val persistentStore = MemStore
  private val assetService = AssetService(persistentStore)
  private val quoteService = QuoteService(persistentStore)
  private val watchlistService = WatchlistService(persistentStore)

  fun root(parent: Router): Route =
    parent.get("/").handler { context ->
      context.response()
        .putHeader("Content-Type", "text/html; charset=UTF-8")
        .end("<center><h1>hello!</h1></center>")
    }

  fun assets(parent: Router): Route =
    parent.get("/assets").handler { context ->
      val assets = assetService.getAll()
      logger.info("Path ${context.normalizedPath()} responds with\n$assets")
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
            logger.debug("Path ${context.normalizedPath()} responds with\n$response")
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
        val symbol = context.pathParam("symbol")
        logger.debug("asset param: $symbol")
        assetService.getBySymbol(symbol)
          .flatMap(quoteService::getForAsset)
          .map { quote ->
            val response = quote.toJson()
            logger.debug("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
            context.response()
              .putHeader("Content-Type", "application/json")
              .end(response.toBuffer())
          }
          .getOrHandle { notFound(context, it) }
      }

  private const val watchlistPath = "/accounts/:accountId/watchlist"

  fun getWatchlist(parent: Router): Route =
    parent.get(watchlistPath)
      .handler { context ->
        Either
          .catch { UUID.fromString(context.pathParam("accountId")) }
          .mapLeft { "Invalid account ID" }
          .flatMap(watchlistService::getWatchlist)
          .map { watchlist ->
            val response = watchlist.toJson()
            logger.debug("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
            context.response()
              .putHeader("Content-Type", "application/json")
              .end(response.toBuffer())
          }
          .getOrHandle { notFound(context, it) }
      }

  fun postWatchlist(parent: Router): Route =
    parent.post(watchlistPath)
      .handler { context ->
        Either
          .catch { UUID.fromString(context.pathParam("accountId")) }
          .mapLeft { "Invalid account ID" }
          .flatMap { accountId ->
            logger.debug("Watchlist for $accountId")
            Either
              .fromNullable(context.bodyAsJson)
              .mapLeft { "Request body not found" }
              .flatMap { body ->
                logger.debug("Watchlist body: $body")
                Either
                  .catch { body.mapTo(Watchlist::class.java) }
                  .mapLeft { it.message ?: "Invalid JSON" }
              }
              .flatMap { watchlist ->
                logger.info("Adding $watchlist for $accountId")
                watchlistService.addWatchlist(accountId, watchlist)
              }
          }
          .map { context.response().setStatusCode(201).end() }
          .getOrHandle { badRequest(context, it) }
      }

  fun deleteWatchlist(parent: Router): Route =
    parent.delete(watchlistPath)
      .handler { context ->
        Either
          .catch { UUID.fromString(context.pathParam("accountId")) }
          .mapLeft { "Invalid account ID" }
          .flatMap { accountId -> watchlistService.deleteWatchlist(accountId) }
          .map { context.response().setStatusCode(200).end() }
          .getOrHandle { badRequest(context, it) }
      }

  private fun notFound(context: RoutingContext, message: String): Future<Void> =
    errorHandler(context, 404, message)

  private fun badRequest(context: RoutingContext, message: String): Future<Void> =
    errorHandler(context, 400, message)

  private fun errorHandler(context: RoutingContext, status: Int, message: String): Future<Void> {
    logger.error(message)
    val response = json {
      obj("message" to message)
    }
    return context.response()
      .setStatusCode(status)
      .setStatusMessage(message)
      .end(response.encodePrettily())
  }
}

