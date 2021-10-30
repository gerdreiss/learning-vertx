package stockbroker

import stockbroker.Model.Asset
import stockbroker.Model.Quote
import arrow.core.Option
import arrow.core.nonEmptyListOf
import arrow.core.toOption
import java.util.concurrent.ThreadLocalRandom

sealed interface PersistentStore {
  fun getAllAssets(): List<Asset>
  fun getAssetBySymbol(symbol: String): Option<Asset>
  fun getQuoteForAsset(asset: Asset): Option<Quote>
}

object MemStore : PersistentStore {
  private val assets = nonEmptyListOf(
    Asset("AAPL"),
    Asset("AMZN"),
    Asset("FB"),
    Asset("GOOG"),
    Asset("MSTF"),
    Asset("NFLX"),
    Asset("TSLA")
  )
  private val quotes = assets.map { asset ->
    Quote(
      asset,
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
    )
  }

  override fun getAllAssets(): List<Asset> =
    assets

  override fun getAssetBySymbol(symbol: String): Option<Asset> =
    assets.find { it.symbol == symbol }.toOption()

  override fun getQuoteForAsset(asset: Asset): Option<Quote> =
    quotes.find { it.asset == asset }.toOption()

}
