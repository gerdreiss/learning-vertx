package stockbroker

import arrow.core.Either
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

}
