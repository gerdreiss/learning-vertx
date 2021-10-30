package com.github.gerdreiss.vertx.stockbroker

import Model.Asset
import arrow.core.Either
import arrow.core.toOption

class AssetService {

  companion object {
    private val ASSETS = listOf(
      Asset("AAPL"),
      Asset("AMZN"),
      Asset("FB"),
      Asset("GOOG"),
      Asset("MSTF"),
      Asset("NFLX"),
      Asset("TSLA")
    )
  }

  fun getAll(): List<Asset> =
    ASSETS

  fun getBySymbol(symbol: String): Either<String, Asset> =
    ASSETS
      .find { it.symbol == symbol }
      .toOption()
      .toEither { "Asset '$symbol' not found" }

}
