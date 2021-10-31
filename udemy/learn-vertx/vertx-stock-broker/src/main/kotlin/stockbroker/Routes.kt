package stockbroker

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router

object Routes {

  private val persistentStore = MemStore
  private val assetService = AssetService(persistentStore)
  private val quoteService = QuoteService(persistentStore)
  private val watchlistService = WatchlistService(persistentStore)

  fun root(parent: Router): Route =
    parent.get("/")
      .respond { context ->
        context.response()
          .putHeader("Content-Type", "text/html; charset=UTF-8")
          .end("<center><h1>hello!</h1></center>")
      }

  fun assets(parent: Router): Route =
    parent.get("/assets")
      .handler(GetAssetsHandler(assetService))

  fun asset(parent: Router): Route =
    parent.get("/assets/:symbol")
      .handler(GetAssetHandler(assetService))

  fun quotes(parent: Router): Route =
    parent.get("/assets/:symbol/quotes")
      .handler(GetQuotesHandler(assetService, quoteService))

  private const val watchlistPath = "/accounts/:accountId/watchlist"

  fun getWatchlist(parent: Router): Route =
    parent.get(watchlistPath)
      .handler(GetWatchlistHandler(watchlistService))

  fun postWatchlist(parent: Router): Route =
    parent.post(watchlistPath)
      .handler(AddWatchlistHandler(watchlistService))

  fun deleteWatchlist(parent: Router): Route =
    parent.delete(watchlistPath)
      .handler(DeleteWatchlistHandler(watchlistService))
}

