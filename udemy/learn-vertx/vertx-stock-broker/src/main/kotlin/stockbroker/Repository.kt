package stockbroker

import arrow.core.Option
import arrow.core.firstOrNone
import arrow.core.nonEmptyListOf
import arrow.core.toOption
import io.vertx.core.Future
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
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

class DbStore(private val db: Pool) : Repository {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(DbStore::class.java)
  }

  override fun getAllAssets(): Future<List<Asset>> =
    db.query("select a.symbol from broker.assets a")
      .execute()
      .map { it.map { row -> Asset(row.getString("symbol")) } }
      .onFailure { logger.error("Fetching assets failed.", it) }

  override fun getAssetBySymbol(symbol: String): Future<Option<Asset>> =
    SqlTemplate
      .forQuery(db, "select * from broker.assets a where a.symbol = #{symbol}")
      .mapTo(AssetEntity::class.java)
      .execute(mapOf("symbol" to symbol))
      .map { rows ->
        rows.firstOrNone()
          .map { entity ->
            entity.toAsset()
          }
      }
      .onFailure { logger.error("Fetching assets failed.", it) }

  override fun getQuoteForAsset(asset: Asset): Future<Option<Quote>> =
    SqlTemplate
      .forQuery(
        db,
        "select q.bid, q.ask, q.last_price, q.volume, q.asset from broker.quotes q where q.asset = #{symbol}"
      )
      .mapTo(QuoteEntity::class.java)
      .execute(mapOf("symbol" to asset.symbol))
      .map { rows ->
        rows.firstOrNone()
          .map { entity ->
            entity.toQuote()
          }
      }
      .onFailure { logger.error("Fetching assets failed.", it) }

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
