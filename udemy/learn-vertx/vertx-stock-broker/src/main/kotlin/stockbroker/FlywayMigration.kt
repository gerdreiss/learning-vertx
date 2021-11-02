package stockbroker

import io.vertx.core.Future
import io.vertx.core.Vertx
import org.flywaydb.core.Flyway
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object FlywayMigration {

  private val logger: Logger = LoggerFactory.getLogger(FlywayMigration::class.java)

  fun migrateDatabase(vertx: Vertx, config: DbConfig): Future<Void> =
    vertx
      .executeBlocking<Void> { promise ->
        val jdbcUrl = "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
        logger.debug("Migrating DB schema using JDBC URL: $jdbcUrl")
        Flyway.configure()
          .locations("db/migration/postgres")
          .dataSource(jdbcUrl, config.dbUser, config.dbPass)
          .schemas("broker")
          .defaultSchema("broker")
          .load()
          .migrate()
          .migrations
          .forEach { migration ->
            logger.debug("${migration.version}: ${migration.description}, elapsed: ${migration.executionTime} seconds")
          }

        promise.complete()
      }
      .onFailure {
        logger.error("Database migration failed with error:\n", it)
      }
}
