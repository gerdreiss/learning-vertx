package com.github.gerdreiss.vertx.stockbroker

import Model.Asset
import Model.Quote
import arrow.core.Either
import arrow.core.some
import java.util.concurrent.ThreadLocalRandom

class QuoteService {

  fun getForAsset(asset: Asset): Either<String, Quote> =
    Quote(
      asset,
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
      ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
    ).some()
      .toEither { "Quotes for asset '${asset.symbol}' not found" }

}
