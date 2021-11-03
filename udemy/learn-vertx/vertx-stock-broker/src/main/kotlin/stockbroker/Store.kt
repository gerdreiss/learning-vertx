package stockbroker

import arrow.core.Option
import arrow.core.firstOrNone
import arrow.core.some
import arrow.core.toOption
import io.vertx.core.Future
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadLocalRandom

sealed interface Store {
  fun saveAsset(asset: Asset): Future<Boolean>
  fun getAllAssets(): Future<List<Asset>>
  fun getAssetBySymbol(symbol: String): Future<Option<Asset>>
  fun getQuoteForAsset(asset: Asset): Future<Option<Quote>>
  fun getWatchlist(accountId: String): Future<Watchlist>
  fun postWatchlist(accountId: String, watchlist: Watchlist): Future<Option<Watchlist>>
  fun deleteWatchlist(accountId: String): Future<Boolean>
}

class DbStore(private val db: Pool) : Store {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(DbStore::class.java)
  }

  override fun saveAsset(asset: Asset): Future<Boolean> =
    SqlTemplate
      .forUpdate(db, "insert into broker.assets values (#{symbol})")
      .execute(mapOf("symbol" to asset.symbol))
      .map { true }
      .onFailure { logger.error("Inserting asset failed.", it) }

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
      .map { it.firstOrNone().map { entity -> entity.toAsset() } }
      .onFailure { logger.error("Fetching assets failed.", it) }

  override fun getQuoteForAsset(asset: Asset): Future<Option<Quote>> =
    SqlTemplate
      .forQuery(
        db,
        "select q.bid, q.ask, q.last_price, q.volume, q.asset from broker.quotes q where q.asset = #{symbol}"
      )
      .mapTo(QuoteEntity::class.java)
      .execute(mapOf("symbol" to asset.symbol))
      .map { it.firstOrNone().map { entity -> entity.toQuote() } }
      .onFailure { logger.error("Fetching assets failed.", it) }

  override fun getWatchlist(accountId: String): Future<Watchlist> =
    SqlTemplate
      .forQuery(db, "select w.asset from broker.watchlists w where w.account_id = #{accountId}")
      .execute(mapOf("accountId" to accountId))
      .map { it.map { row -> Asset(row.getString("asset")) } }
      .map { Watchlist(it) }
      .onFailure { logger.error("Fetching watchlists for account ID '$accountId' failed.", it) }

  override fun postWatchlist(accountId: String, watchlist: Watchlist): Future<Option<Watchlist>> =
    SqlTemplate
      .forUpdate(db, "insert into broker.watchlists values (#{accountId}, #{asset})")
      .executeBatch(watchlist.assets.map { asset -> mapOf("accountId" to accountId, "asset" to asset.symbol) })
      .map { watchlist.some() }
      .onFailure { logger.error("Inserting watchlists for account ID '$accountId' failed.", it) }

  override fun deleteWatchlist(accountId: String): Future<Boolean> =
    SqlTemplate
      .forUpdate(db, "delete from broker.watchlists w where w.account_id = #{accountId}")
      .execute(mapOf("accountId" to accountId))
      .map(true)
      .onFailure { logger.error("Deleting watchlists for account ID '$accountId' failed.", it) }
}

object MemStore : Store {

  private val watchlists = mutableMapOf<String, Watchlist>()

  private val assets = mutableListOf(
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

  override fun saveAsset(asset: Asset): Future<Boolean> =
    Future.succeededFuture(assets.add(asset))

  override fun getAllAssets(): Future<List<Asset>> =
    Future.succeededFuture(assets)

  override fun getAssetBySymbol(symbol: String): Future<Option<Asset>> =
    Future.succeededFuture(assets.find { it.symbol == symbol }.toOption())

  override fun getQuoteForAsset(asset: Asset): Future<Option<Quote>> =
    Future.succeededFuture(quotes.find { it.asset == asset }.toOption())

  override fun getWatchlist(accountId: String): Future<Watchlist> =
    Future.succeededFuture(watchlists[accountId] ?: Watchlist(listOf()))

  override fun postWatchlist(accountId: String, watchlist: Watchlist): Future<Option<Watchlist>> =
    Future.succeededFuture(watchlists.put(accountId, watchlist).toOption())

  override fun deleteWatchlist(accountId: String): Future<Boolean> =
    Future.succeededFuture(watchlists.remove(accountId) != null)
}
