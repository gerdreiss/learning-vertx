package stockbroker

import arrow.core.Either
import arrow.core.Option
import io.vertx.core.Future
import io.vertx.core.Vertx
import java.util.UUID

class Services(
  val assetService: AssetService,
  val quoteService: QuoteService,
  val watchlistService: WatchlistService
) {
  companion object {
    fun create(vertx: Vertx, brokerConfig: BrokerConfig): Services {
      val persistentStore = PgStore(brokerConfig.pgPool(vertx))
      val assetService = AssetService(persistentStore)
      val quoteService = QuoteService(persistentStore)
      val watchlistService = WatchlistService(persistentStore)
      return Services(assetService, quoteService, watchlistService)
    }
  }
}

class AssetService(private val store: Repository) {
  fun getAll(): Future<List<Asset>> =
    store.getAllAssets()

  fun getBySymbol(symbol: String): Future<Either<String, Asset>> =
    store.getAssetBySymbol(symbol)
      .map { it.toEither { "Asset '$symbol' not found" } }
}

class QuoteService(private val store: Repository) {
  fun getForAsset(asset: Asset): Future<Either<String, Quote>> =
    store.getQuoteForAsset(asset)
      .map { it.toEither { "Quotes for asset '${asset.symbol}' not found" } }
}

class WatchlistService(private val store: Repository) {
  fun getWatchlist(accountId: UUID): Future<Either<String, Watchlist>> =
    store.getWatchlist(accountId)
      .map { it.toEither { "Watch list for account $accountId not found" } }

  fun addWatchlist(accountId: UUID, watchlist: Watchlist): Future<Either<String, Option<Watchlist>>> =
    store.putWatchlist(accountId, watchlist)
      .map { Either.Right(it) }

  fun deleteWatchlist(accountId: UUID): Future<Either<String, Option<Watchlist>>> =
    store.deleteWatchlist(accountId)
      .map { Either.Right(it) }
}
