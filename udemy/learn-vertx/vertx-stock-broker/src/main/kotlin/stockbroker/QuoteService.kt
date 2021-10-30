package stockbroker

import stockbroker.Model.Asset
import stockbroker.Model.Quote
import arrow.core.Either

class QuoteService(private val store: PersistentStore) {

  fun getForAsset(asset: Asset): Either<String, Quote> =
    store.getQuoteForAsset(asset)
      .toEither { "Quotes for asset '${asset.symbol}' not found" }

}
