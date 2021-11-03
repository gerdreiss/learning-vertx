package stockbroker

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

data class AssetEntity(val symbol: String) {
  constructor() : this("")

  fun toAsset(): Asset = Asset(symbol)
}

data class QuoteEntity(
  @JsonProperty("bid")
  val bid: BigDecimal,
  @JsonProperty("ask")
  val ask: BigDecimal,
  @JsonProperty("last_price")
  val lastPrice: BigDecimal,
  @JsonProperty("volume")
  val volume: BigDecimal,
  @JsonProperty("asset")
  val asset: String
) {
  constructor() : this(ZERO, ZERO, ZERO, ZERO, "")

  fun toQuote(): Quotes = Quotes(Asset(asset), bid, ask, lastPrice, volume)
}
