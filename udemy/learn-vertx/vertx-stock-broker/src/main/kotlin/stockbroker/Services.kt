package stockbroker

import arrow.core.Either
import arrow.core.some
import java.util.*

class AssetService(private val store: Repository) {

  fun getAll(): List<Asset> =
    store.getAllAssets()

  fun getBySymbol(symbol: String): Either<String, Asset> =
    store.getAssetBySymbol(symbol)
      .toEither { "Asset '$symbol' not found" }

}

class QuoteService(private val store: Repository) {

  fun getForAsset(asset: Asset): Either<String, Quote> =
    store.getQuoteForAsset(asset)
      .toEither { "Quotes for asset '${asset.symbol}' not found" }

}

class WatchlistService(private val store: Repository) {

  fun getWatchlist(accountId: UUID): Either<String, Watchlist> =
    store.getWatchlist(accountId)
      .toEither { "Watch list for account $accountId not found" }

  fun addWatchlist(accountId: UUID, watchlist: Watchlist) =
    store.putWatchlist(accountId, watchlist)
      .flatMap { watchlist.some() }
      .toEither { "Failed to add $watchlist" }

  fun deleteWatchlist(accountId: UUID) =
    store.deleteWatchlist(accountId)
      .toEither { "Failed to delete watchlist for $accountId" }
}
