package stockbroker

import arrow.core.Either
import arrow.core.Option
import io.vertx.core.Future
import io.vertx.core.Vertx
import java.util.*

class Services(
  val assetService: AssetService,
  val quoteService: QuotesService,
  val watchlistService: WatchlistService
) {
  companion object {
    fun create(vertx: Vertx, brokerConfig: BrokerConfig): Services {
      val persistentStore = DbGateway(brokerConfig.dbConnectionPool(vertx))
      val assetService = AssetService(persistentStore)
      val quoteService = QuotesService(persistentStore)
      val watchlistService = WatchlistService(persistentStore)
      return Services(assetService, quoteService, watchlistService)
    }
  }
}

class AssetService(private val store: Gateway) {
  fun getAllAssets(): Future<List<Asset>> =
    store.getAllAssets()

  fun getAssetBySymbol(symbol: String): Future<Either<String, Asset>> =
    store.getAssetBySymbol(symbol)
      .map { it.toEither { "Asset '$symbol' not found" } }

  fun addAsset(asset: Asset): Future<Either<String, Asset>> =
    store.addAsset(asset)
      .map { Either.Right(asset) }
}

class QuotesService(private val store: Gateway) {
  fun getQuotesForAsset(asset: Asset): Future<Either<String, Quotes>> =
    store.getQuotesForAsset(asset)
      .map { it.toEither { "Quotes for asset '${asset.symbol}' not found" } }
}

class WatchlistService(private val gateway: Gateway) {
  fun getWatchlist(accountId: UUID): Future<Either<String, Watchlist>> =
    gateway.getWatchlist(accountId.toString())
      .map { Either.Right(it) }

  fun addWatchlist(accountId: UUID, watchlist: Watchlist): Future<Either<String, Option<Watchlist>>> =
    gateway.postWatchlist(accountId.toString(), watchlist)
      .map { Either.Right(it) }

  fun deleteWatchlist(accountId: UUID): Future<Either<String, Boolean>> =
    gateway.deleteWatchlist(accountId.toString())
      .map { Either.Right(it) }
}
