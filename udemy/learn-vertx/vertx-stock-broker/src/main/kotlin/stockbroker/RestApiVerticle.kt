package stockbroker

import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.net.SocketAddress
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
          val config = ServerConfig.fromConfig(result.result())
          vertx
            .createHttpServer()
            .requestHandler(Routes.routes(vertx))
            .exceptionHandler { logger.error("HTTP server error: $it") }
            .listen(SocketAddress.inetSocketAddress(config.port, config.host)) { http ->
              if (http.succeeded()) {
                startPromise.complete()
                logger.info("HTTP server started on port ${config.port}")
              } else {
                startPromise.fail(http.cause())
              }
            }
        }
      }
  }
}
