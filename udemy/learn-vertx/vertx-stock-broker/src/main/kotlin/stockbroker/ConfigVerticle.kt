package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConfigVerticle : AbstractVerticle() {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(ConfigVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { config ->
        logger.info("Config loaded: $config")
        startPromise.complete()
      }
  }

}
