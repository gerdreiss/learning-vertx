package stockbroker

import arrow.core.NonEmptyList
import io.vertx.core.json.JsonObject
import java.math.BigDecimal


data class Asset(val symbol: String) {
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

data class Watchlist(val assets: NonEmptyList<Asset>) {
  fun toJson(): JsonObject = JsonObject.mapFrom(this)
}


