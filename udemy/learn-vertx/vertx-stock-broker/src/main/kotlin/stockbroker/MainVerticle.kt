package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max

class MainVerticle : AbstractVerticle() {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader.load(vertx)
      .compose { config ->
        logger.debug("Config: {}", config)
        val deploymentOptions = DeploymentOptions()
          .setInstances(processors())
          .setConfig(config)
        vertx
          .deployVerticle(RestApiVerticle::class.java.name, deploymentOptions)
          .onFailure(startPromise::fail)
          .onSuccess {
            logger.info("Deployed {} with id {}", RestApiVerticle::class.java.simpleName, it)
            startPromise.complete()
          }
      }
  }

  private fun processors(): Int =
    // to ensure that there is no problem in virtualized containers
    max(1, Runtime.getRuntime().availableProcessors())

}
