package stockbroker

import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RestApiVerticle : AbstractVerticle() {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(RestApiVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {
    ConfigRetriever.create(vertx)
      .getConfig { result ->
        if (result.failed()) {
          startPromise.fail(result.cause())
        } else {

          val brokerConfig = BrokerConfig.fromJson(result.result())
          val services = Services.create(vertx, brokerConfig)
          val routes = Routes.create(vertx, services)

          vertx
            .createHttpServer()
            .requestHandler(routes)
            .exceptionHandler { logger.error("HTTP server error: $it") }
            .listen(brokerConfig.socketAddress()) { httpServer ->
              if (httpServer.succeeded()) {
                startPromise.complete()
                logger.info("HTTP server started on port ${httpServer.result().actualPort()}")
              } else {
                startPromise.fail(httpServer.cause())
              }
            }
        }
      }
  }
}
