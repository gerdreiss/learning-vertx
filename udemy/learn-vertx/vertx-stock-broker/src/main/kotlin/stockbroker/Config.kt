package stockbroker

import io.vertx.config.ConfigRetriever
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.net.SocketAddress
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Pool

object ConfigLoader {

  const val SERVER_PORT = "SERVER_PORT"
  const val SERVER_HOST = "SERVER_HOST"
  const val DATABASE_HOST = "DATABASE_HOST"
  const val DATABASE_PORT = "DATABASE_PORT"
  const val DATABASE_NAME = "DATABASE_NAME"
  const val DATABASE_USER = "DATABASE_USER"
  const val DATABASE_PASSWORD = "DATABASE_PASSWORD"

  private val EXPOSED_KEYS = jsonArrayOf(
    SERVER_PORT,
    SERVER_HOST,
    DATABASE_HOST,
    DATABASE_PORT,
    DATABASE_NAME,
    DATABASE_USER,
    DATABASE_PASSWORD
  )

  fun load(vertx: Vertx): Future<JsonObject> =
    ConfigRetriever
      .create(
        vertx,
        configRetrieverOptionsOf(
          stores = listOf(
            configStoreOptionsOf(
              type = "file",
              format = "yaml",
              config = json { obj("path" to "application.yml") }
            ),
            configStoreOptionsOf(
              type = "sys",
              config = json { obj("cache" to false) }
            ),
            configStoreOptionsOf(
              type = "env",
              config = json { obj("keys" to EXPOSED_KEYS) }
            )
          )
        )
      )
      .config
}

class BrokerConfig(
  private val serverConfig: ServerConfig,
  private val dbConfig: DbConfig
) {
  companion object {
    fun fromJson(json: JsonObject) =
      BrokerConfig(
        ServerConfig.fromJson(json),
        DbConfig.fromJson(json)
      )
  }

  fun socketAddress(): SocketAddress =
    SocketAddress.inetSocketAddress(serverConfig.port, serverConfig.host)

  fun dbConnectionPool(vertx: Vertx): Pool =
    PgPool.pool(
      vertx,
      pgConnectOptionsOf(
        host = dbConfig.dbHost,
        port = dbConfig.dbPort,
        database = dbConfig.dbName,
        user = dbConfig.dbUser,
        password = dbConfig.dbPass
      ),
      poolOptionsOf(
        maxSize = 4
      )
    )
}

class DbConfig(
  val dbHost: String,
  val dbPort: Int,
  val dbName: String,
  val dbUser: String,
  val dbPass: String
) {
  companion object {
    fun fromJson(json: JsonObject) =
      DbConfig(
        dbHost = json.getString(ConfigLoader.DATABASE_HOST),
        dbPort = json.getInteger(ConfigLoader.DATABASE_PORT),
        dbName = json.getString(ConfigLoader.DATABASE_NAME),
        dbUser = json.getString(ConfigLoader.DATABASE_USER),
        dbPass = json.getString(ConfigLoader.DATABASE_PASSWORD),
      )
  }
}

class ServerConfig(
  val host: String,
  val port: Int
) {
  companion object {
    fun fromJson(json: JsonObject) =
      ServerConfig(
        host = json.getString(ConfigLoader.SERVER_HOST),
        port = json.getInteger(ConfigLoader.SERVER_PORT)
      )
  }
}
