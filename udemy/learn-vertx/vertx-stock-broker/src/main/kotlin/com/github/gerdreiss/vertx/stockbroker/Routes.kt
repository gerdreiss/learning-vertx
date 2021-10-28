package com.github.gerdreiss.vertx.stockbroker


import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Routes {

  private val LOG: Logger = LoggerFactory.getLogger(Routes::class.java)

  private val assetService = AssetService()
  private val quoteService = QuoteService()

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
    parent.get("/assets/:symbol").handler { context ->
      val asset = assetService.getBySymbol(context.pathParam("symbol"))
      LOG.info("Path ${context.normalizedPath()} responds with\n$asset")
      context.response()
        .putHeader("Content-Type", "application/json")
        .end(asset.toJson().toBuffer())
    }

  fun quotes(parent: Router) =
    parent.get("/assets/:asset/quotes").handler { context ->
      val asset = context.pathParam("asset")
      LOG.debug("asset param: $asset")
      val quote = quoteService.getForAsset(asset)
      val response = quote.toJson()
      LOG.info("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer())
    }

}
