package stockbroker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kotlin.core.deploymentOptionsOf
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
        logger.debug("Config loaded: {}", config)
        vertx
          .executeBlocking<DbConfig> { promise ->
            promise.complete(DbConfig.fromJson(config))
          }
          .flatMap { dbConfig ->
            logger.info("Start DB migration...")
            FlywayMigration.migrateDatabase(vertx, dbConfig)
              .onSuccess {
                logger.info("DB migration finished...")
              }
          }
          .flatMap {
            logger.info("Deploying verticles...")
            val deploymentOptions = deploymentOptionsOf(instances = processors(), config = config)
            vertx
              .deployVerticle(RestApiVerticle::class.java, deploymentOptions)
              .onSuccess {
                logger.info("Deployed {} with id {}..", RestApiVerticle::class.java.simpleName, it)
              }
          }
      }
      .onSuccess { startPromise.complete() }
      .onFailure(startPromise::fail)
  }

  private fun processors(): Int =
    // to ensure that there is no problem in virtualized containers
    max(1, Runtime.getRuntime().availableProcessors())
}
