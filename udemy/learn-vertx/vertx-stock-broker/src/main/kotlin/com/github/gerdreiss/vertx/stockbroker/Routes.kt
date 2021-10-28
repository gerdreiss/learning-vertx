package com.github.gerdreiss.vertx.stockbroker

import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Routes {

  val LOG: Logger = LoggerFactory.getLogger(Routes::class.java)

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


  data class Asset(val symbol: String)
}
