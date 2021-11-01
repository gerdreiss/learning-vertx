package stockbroker

import io.vertx.config.ConfigRetriever
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.obj

object ConfigLoader {

  private const val SERVER_PORT = "SERVER_PORT"

  fun load(vertx: Vertx): Future<BrokerConfig> {

    val retrieverOptions = configRetrieverOptionsOf(
      stores = listOf(
        configStoreOptionsOf(
          type = "file",
          format = "yaml",
          config = json {
            obj("path" to "application.yml")
          }
        ),
        configStoreOptionsOf(
          type = "sys",
          config = json {
            obj("keys" to jsonArrayOf(SERVER_PORT))
          }
        ),
        configStoreOptionsOf(
          type = "env",
          config = json {
            obj("keys" to jsonArrayOf(SERVER_PORT))
          }
        )
      )
    )

    return ConfigRetriever
      .create(vertx, retrieverOptions)
      .config
      .map {
        BrokerConfig(it.getInteger(SERVER_PORT))
      }
  }
}

data class BrokerConfig(val serverPort: Int = 8888)
