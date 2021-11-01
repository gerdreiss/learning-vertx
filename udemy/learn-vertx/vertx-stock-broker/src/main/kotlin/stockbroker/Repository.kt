package stockbroker

import arrow.core.Option
import arrow.core.nonEmptyListOf
import arrow.core.toOption
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

sealed interface Repository {
  fun getAllAssets(): List<Asset>
  fun getAssetBySymbol(symbol: String): Option<Asset>
  fun getQuoteForAsset(asset: Asset): Option<Quote>
  fun getWatchlist(accountId: UUID): Option<Watchlist>
  fun putWatchlist(accountId: UUID, watchlist: Watchlist): Option<Watchlist>
  fun deleteWatchlist(accountId: UUID): Option<Watchlist>
}

object MemStore : Repository {
  private val watchlists = mutableMapOf<UUID, Watchlist>()
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
    assets.find { it.symbol == symbol }
      .toOption()

  override fun getQuoteForAsset(asset: Asset): Option<Quote> =
    quotes.find { it.asset == asset }
      .toOption()

  override fun getWatchlist(accountId: UUID): Option<Watchlist> =
    watchlists[accountId]
      .toOption()

  override fun putWatchlist(accountId: UUID, watchlist: Watchlist): Option<Watchlist> =
    watchlists.put(accountId, watchlist)
      .toOption()

  override fun deleteWatchlist(accountId: UUID): Option<Watchlist> =
    watchlists.remove(accountId)
      .toOption()
}
