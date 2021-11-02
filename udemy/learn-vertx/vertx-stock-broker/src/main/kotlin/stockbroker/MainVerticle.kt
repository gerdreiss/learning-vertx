package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max

class MainVerticle : AbstractVerticle() {

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader.load(vertx)
      .flatMap { config ->
        logger.debug("Config: {}", config)
        FlywayMigration.migrateDatabase(vertx, DbConfig.fromConfig(config))
          .flatMap {
            logger.debug("DB migration finished...")
            deployVerticle(RestApiVerticle::class.java, config, startPromise)
              .onSuccess {
                startPromise.complete()
              }
          }
      }
  }

  private fun <T : AbstractVerticle> deployVerticle(
    clazz: Class<T>,
    config: JsonObject,
    startPromise: Promise<Void>
  ): Future<String> {
    val deploymentOptions = DeploymentOptions()
      .setInstances(processors())
      .setConfig(config)
    return vertx
      .deployVerticle(clazz.name, deploymentOptions)
      .onFailure(startPromise::fail)
      .onSuccess {
        logger.info("Deployed {} with id {}..", RestApiVerticle::class.java.simpleName, it)
      }
  }

  private fun processors(): Int =
    // to ensure that there is no problem in virtualized containers
    max(1, Runtime.getRuntime().availableProcessors())
}
