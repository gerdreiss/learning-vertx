package stockbroker

import stockbroker.Model.Asset
import arrow.core.Either

class AssetService(private val store: PersistentStore) {

  fun getAll(): List<Asset> =
    store.getAllAssets()

  fun getBySymbol(symbol: String): Either<String, Asset> =
    store.getAssetBySymbol(symbol)
      .toEither { "Asset '$symbol' not found" }

}
