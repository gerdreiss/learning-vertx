package com.github.gerdreiss.vertx.stockbroker

import java.util.concurrent.ThreadLocalRandom

class QuoteService {
  fun getForAsset(symbol: String) = Model.Quote(
    Model.Asset(symbol),
    ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
    ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
    ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
    ThreadLocalRandom.current().nextDouble(1.0, 100.0).toBigDecimal(),
  )
}
