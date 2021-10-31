package stockbroker

import io.vertx.core.json.JsonObject
import java.math.BigDecimal


data class Asset(val symbol: String) {
  constructor(): this("")
  fun toJson(): JsonObject = JsonObject.mapFrom(this)
}

data class Quote(
  val asset: Asset,
  val bid: BigDecimal,
  val ask: BigDecimal,
  val lastPrice: BigDecimal,
  val volume: BigDecimal
) {
  fun toJson(): JsonObject = JsonObject.mapFrom(this)
}

data class Watchlist(var assets: List<Asset>) {
  constructor() : this(listOf())
  fun toJson(): JsonObject = JsonObject.mapFrom(this)
}
