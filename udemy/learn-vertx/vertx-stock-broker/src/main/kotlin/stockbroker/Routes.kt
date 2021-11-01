package stockbroker

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

object Routes {

  private val persistentStore = MemStore
  private val assetService = AssetService(persistentStore)
  private val quoteService = QuoteService(persistentStore)
  private val watchlistService = WatchlistService(persistentStore)

  fun routes(vertx: Vertx): Router {
    val parent = Router.router(vertx)

    parent.route()
      .handler(BodyHandler.create())
      .failureHandler(FailureHandler())

    parent.get("/")
      .respond { context ->
        context.response()
          .putHeader("Content-Type", "text/html; charset=UTF-8")
          .end("<center><h1>hello!</h1></center>")
      }

    val assetsPath = "/assets"
    val assetBySymbolPath = "$assetsPath/:symbol"
    val quotesPath = "$assetBySymbolPath/quotes"
    val accountsPath = "/accounts"
    val accountByIdPath = "$accountsPath/:accountId"
    val watchlistPath = "$accountByIdPath/watchlist"

    parent.get(assetsPath).handler(GetAssetsHandler(assetService))
    parent.get(assetBySymbolPath).handler(GetAssetHandler(assetService))
    parent.get(quotesPath).handler(GetQuotesHandler(assetService, quoteService))
    parent.get(watchlistPath).handler(GetWatchlistHandler(watchlistService))
    parent.post(watchlistPath).handler(AddWatchlistHandler(watchlistService))
    parent.delete(watchlistPath).handler(DeleteWatchlistHandler(watchlistService))

    return parent
  }
}
