package stockbroker

import arrow.core.Option
import arrow.core.nonEmptyListOf
import arrow.core.toOption
import io.vertx.core.Future
import io.vertx.pgclient.PgPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

sealed interface Repository {
  fun getAllAssets(): Future<List<Asset>>
  fun getAssetBySymbol(symbol: String): Future<Option<Asset>>
  fun getQuoteForAsset(asset: Asset): Future<Option<Quote>>
  fun getWatchlist(accountId: UUID): Future<Option<Watchlist>>
  fun putWatchlist(accountId: UUID, watchlist: Watchlist): Future<Option<Watchlist>>
  fun deleteWatchlist(accountId: UUID): Future<Option<Watchlist>>
}

class PgStore(private val db: PgPool) : Repository {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(PgStore::class.java)
  }

  override fun getAllAssets(): Future<List<Asset>> =
    db.query("select a.symbol from broker.assets a")
      .execute()
      .map { it.map { row -> Asset(row.getString("symbol")) } }
      .onFailure { logger.error("Fetching assets failed.", it) }

  override fun getAssetBySymbol(symbol: String): Future<Option<Asset>> {
    TODO("Not yet implemented")
  }

  override fun getQuoteForAsset(asset: Asset): Future<Option<Quote>> {
    TODO("Not yet implemented")
  }

  override fun getWatchlist(accountId: UUID): Future<Option<Watchlist>> {
    TODO("Not yet implemented")
  }

  override fun putWatchlist(accountId: UUID, watchlist: Watchlist): Future<Option<Watchlist>> {
    TODO("Not yet implemented")
  }

  override fun deleteWatchlist(accountId: UUID): Future<Option<Watchlist>> {
    TODO("Not yet implemented")
  }
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

  override fun getAllAssets(): Future<List<Asset>> =
    Future.succeededFuture(assets)

  override fun getAssetBySymbol(symbol: String): Future<Option<Asset>> =
    Future.succeededFuture(assets.find { it.symbol == symbol }.toOption())

  override fun getQuoteForAsset(asset: Asset): Future<Option<Quote>> =
    Future.succeededFuture(quotes.find { it.asset == asset }.toOption())

  override fun getWatchlist(accountId: UUID): Future<Option<Watchlist>> =
    Future.succeededFuture(watchlists[accountId].toOption())

  override fun putWatchlist(accountId: UUID, watchlist: Watchlist): Future<Option<Watchlist>> =
    Future.succeededFuture(watchlists.put(accountId, watchlist).toOption())

  override fun deleteWatchlist(accountId: UUID): Future<Option<Watchlist>> =
    Future.succeededFuture(watchlists.remove(accountId).toOption())
}
