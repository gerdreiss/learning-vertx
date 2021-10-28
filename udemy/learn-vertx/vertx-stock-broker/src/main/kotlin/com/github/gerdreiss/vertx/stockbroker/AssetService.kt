package com.github.gerdreiss.vertx.stockbroker

class AssetService {

  fun getAll(): List<Model.Asset> = listOf(
    Model.Asset("AAPL"),
    Model.Asset("NFLX"),
    Model.Asset("AMZN"),
    Model.Asset("TSLA")
  )

  fun getBySymbol(symbol: String) = Model.Asset(symbol)

}
