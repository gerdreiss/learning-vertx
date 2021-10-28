package com.github.gerdreiss.vertx.stockbroker

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

object Routes {

  private val LOG: Logger = LoggerFactory.getLogger(Routes::class.java)

  fun root(parent: Router): Route =
    parent.get("/").handler { context ->
      context.response()
        .putHeader("Content-Type", "text/html; charset=UTF-8")
        .end("<center><h1>hello!</h1></center>")
    }

  fun assets(parent: Router): Route =
    parent.get("/assets").handler { context ->
      val assets = listOf(
        Asset("AAPL"),
        Asset("NFLX"),
        Asset("AMZN"),
        Asset("TSLA")
      )
      LOG.info("Path ${context.normalizedPath()} responds with\n$assets")
      val response = assets.fold(JsonArray()) { jsonArray: JsonArray, asset: Asset ->
        jsonArray.add(asset)
      }
      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer())
    }

  fun asset(parent: Router): Route =
    parent.get("/assets/:symbol").handler { context ->
      val asset = Asset(context.pathParam("symbol"))
      LOG.info("Path ${context.normalizedPath()} responds with\n$asset")
      val response = JsonObject.mapFrom(asset)
      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer())
    }

  fun quotes(parent: Router) {
    parent.get("/assets/:asset/quotes").handler { context ->
      val asset = context.pathParam("asset")
      LOG.debug("asset param: $asset")
      val quote = Quote(
        Asset(asset),
        ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
        ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
        ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
        ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      )
      val response = quote.toJson()
      LOG.info("Path ${context.normalizedPath()} responds with ${response.encodePrettily()}")
      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer())
    }

  }

  data class Asset(val symbol: String)

  data class Quote(
    val asset: Asset,
    val bid: BigDecimal,
    val ask: BigDecimal,
    val lastPrice: BigDecimal,
    val volume: BigDecimal
  ) {
    fun toJson(): JsonObject = JsonObject.mapFrom(this)
  }

}
