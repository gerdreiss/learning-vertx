package stockbroker

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrHandle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class AbstractHandler : Handler<RoutingContext> {

  protected abstract val logger: Logger

  protected fun notFound(context: RoutingContext, message: String): Future<Void> =
    errorHandler(context, 404, message)

  protected fun badRequest(context: RoutingContext, message: String): Future<Void> =
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

class GetAssetsHandler(
  private val assetService: AssetService
) : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(GetAssetsHandler::class.java)

  override fun handle(context: RoutingContext) {
    val assets = assetService.getAll()
    logger.info("Path ${context.normalizedPath()} responds with\n$assets")
    val response = assets.fold(JsonArray()) { jsonArray, asset ->
      jsonArray.add(asset.toJson())
    }
    context.json(response)
  }

}

class GetAssetHandler(
  private val assetService: AssetService
) : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(GetAssetHandler::class.java)

  override fun handle(context: RoutingContext) {
    assetService.getBySymbol(context.pathParam("symbol"))
      .map { asset ->
        val response = asset.toJson()
        logger.debug("Path ${context.normalizedPath()} responds with\n$response")
        context.json(response)
      }
      .getOrHandle { notFound(context, it) }
  }
}

class GetQuotesHandler(
  private val assetService: AssetService,
  private val quoteService: QuoteService
) : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(GetQuotesHandler::class.java)

  override fun handle(context: RoutingContext) {
    val symbol = context.pathParam("symbol")
    logger.debug("asset param: $symbol")
    assetService.getBySymbol(symbol)
      .flatMap(quoteService::getForAsset)
      .map { quote ->
        val response = quote.toJson()
        logger.debug("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
        context.json(response)
      }
      .getOrHandle { notFound(context, it) }
  }
}

class GetWatchlistHandler(
  private val watchlistService: WatchlistService
) : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(GetWatchlistHandler::class.java)

  override fun handle(context: RoutingContext) {
    Either
      .catch { UUID.fromString(context.pathParam("accountId")) }
      .mapLeft { "Invalid account ID" }
      .flatMap(watchlistService::getWatchlist)
      .map { watchlist ->
        val response = watchlist.toJson()
        logger.debug("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
        context.json(response)
      }
      .getOrHandle { notFound(context, it) }
  }
}

class AddWatchlistHandler(
  private val watchlistService: WatchlistService
) : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(AddWatchlistHandler::class.java)

  override fun handle(context: RoutingContext) {
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
}

class DeleteWatchlistHandler(
  private val watchlistService: WatchlistService
) : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(DeleteWatchlistHandler::class.java)

  override fun handle(context: RoutingContext) {
    Either
      .catch { UUID.fromString(context.pathParam("accountId")) }
      .mapLeft { "Invalid account ID" }
      .flatMap { accountId -> watchlistService.deleteWatchlist(accountId) }
      .map { context.response().setStatusCode(200).end() }
      .getOrHandle { badRequest(context, it) }
  }
}

class FailureHandler : AbstractHandler() {
  override val logger: Logger
    get() = LoggerFactory.getLogger(FailureHandler::class.java)

  override fun handle(errorContext: RoutingContext) {
    if (errorContext.response().ended()) {
      // ignore
    } else {
      val failure = errorContext.failure()
      logger.error("Route error occurred", failure)
      val response = json {
        obj("message" to (failure.message ?: "Unknown error occurred"))
      }
      errorContext.response()
        .setStatusCode(errorContext.statusCode())
        .end(response.toBuffer())
    }
  }

}
