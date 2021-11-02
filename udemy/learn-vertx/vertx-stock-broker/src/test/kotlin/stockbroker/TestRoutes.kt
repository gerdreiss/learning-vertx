package stockbroker

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

@ExtendWith(VertxExtension::class)
class TestRoutes {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    System.setProperty("SERVER_PORT", "9001")
    System.setProperty("SERVER_HOST", "0.0.0.0")
    System.setProperty("DATABASE_HOST", "localhost")
    System.setProperty("DATABASE_PORT", "5432")
    System.setProperty("DATABASE_NAME", "vertx-stock-broker")
    System.setProperty("DATABASE_USER", "postgres")
    System.setProperty("DATABASE_PASSWORD", "secret")
    vertx.deployVerticle(MainVerticle(), testContext.succeeding { testContext.completeNow() })
  }

  @Test
  fun returns_all_assets(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(System.getProperty("SERVER_PORT").toInt()))
      .get("/assets")
      .send()
      .onComplete(
        testContext.succeeding { response ->
          val json = response.bodyAsJsonArray()
          println(json)
          assertEquals(
            """[{"symbol":"AAPL"},{"symbol":"AMZN"},{"symbol":"FB"},{"symbol":"GOOG"},{"symbol":"MSTF"},{"symbol":"NFLX"},{"symbol":"TSLA"}]""",
            json.encode()
          )
          assertEquals(200, response.statusCode())
          testContext.completeNow()
        }
      )
  }

  @Test
  fun returns_asset(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(System.getProperty("SERVER_PORT").toInt()))
      .get("/assets/AMZN")
      .send()
      .onComplete(
        testContext.succeeding { response ->
          val json = response.bodyAsJsonObject()
          println(json)
          assertEquals("""{"symbol":"AMZN"}""", json.encode())
          assertEquals(200, response.statusCode())
          testContext.completeNow()
        }
      )
  }

  @Test
  fun returns_quotes_for_asset(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(System.getProperty("SERVER_PORT").toInt()))
      .get("/assets/AMZN/quotes")
      .send()
      .onComplete(
        testContext.succeeding { response ->
          val json = response.bodyAsJsonObject()
          println(json)
          assertEquals("AMZN", json.getJsonObject("asset").getString("symbol"))
          assertEquals(200, response.statusCode())
          testContext.completeNow()
        }
      )
  }

  @Test
  fun test_post_invalid_accountId(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(System.getProperty("SERVER_PORT").toInt()))
      .post("/accounts/${ThreadLocalRandom.current().nextInt()}/watchlist")
      .send()
      .onComplete(
        testContext.succeeding { response ->
          assertEquals(400, response.statusCode())
          testContext.completeNow()
        }
      )
  }

  @Test
  fun test_post_no_body(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(System.getProperty("SERVER_PORT").toInt()))
      .post("/accounts/${UUID.randomUUID()}/watchlist")
      .send()
      .onComplete(
        testContext.succeeding { response ->
          assertEquals(400, response.statusCode())
          testContext.completeNow()
        }
      )
  }

  @Test
  fun test_post_get_and_delete(vertx: Vertx, testContext: VertxTestContext) {
    val webClient = WebClient.create(vertx, WebClientOptions().setDefaultPort(System.getProperty("SERVER_PORT").toInt()))
    val url = "/accounts/${UUID.randomUUID()}/watchlist"
    val watchlist = Watchlist(listOf(Asset("APPL"), Asset("FP"))).toJson()

    webClient
      .post(url)
      .sendJsonObject(watchlist)
      .onComplete(
        testContext.succeeding { response ->
          assertEquals(201, response.statusCode())
        }
      )
      .flatMap {
        webClient
          .get(url)
          .send()
          .onComplete(
            testContext.succeeding { response ->
              assertEquals(200, response.statusCode())
              assertEquals(watchlist, response.bodyAsJsonObject())
            }
          )
      }
      .flatMap {
        webClient
          .delete(url)
          .send()
          .onComplete(
            testContext.succeeding { response ->
              assertEquals(200, response.statusCode())
            }
          )
      }
      .onSuccess { testContext.completeNow() }
  }
}
