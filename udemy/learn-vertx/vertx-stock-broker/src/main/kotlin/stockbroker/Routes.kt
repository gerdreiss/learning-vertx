package stockbroker

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

object Routes {

  fun routes(vertx: Vertx, services: Services): Router {

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

    parent.get(assetsPath).handler(GetAssetsHandler(services.assetService))
    parent.post(assetsPath).handler(PostAssetHandler(services.assetService))
    parent.get(assetBySymbolPath).handler(GetAssetHandler(services.assetService))
    parent.get(quotesPath).handler(GetQuotesHandler(services.assetService, services.quoteService))
    parent.get(watchlistPath).handler(GetWatchlistHandler(services.watchlistService))
    parent.post(watchlistPath).handler(AddWatchlistHandler(services.watchlistService))
    parent.delete(watchlistPath).handler(DeleteWatchlistHandler(services.watchlistService))

    return parent
  }
}
