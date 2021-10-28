package com.github.gerdreiss.vertx.stockbroker

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestRoutes {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding { testContext.completeNow() })
  }

  @Test
  fun returns_all_assets(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(8888))
      .get("/assets")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonArray()
        println(json)
        assertEquals("""[{"symbol":"AAPL"},{"symbol":"NFLX"},{"symbol":"AMZN"},{"symbol":"TSLA"}]""", json.encode())
        assertEquals(200, response.statusCode())
        testContext.completeNow()
      })
  }

  @Test
  fun returns_asset(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(8888))
      .get("/assets/AMZN")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        println(json)
        assertEquals("""{"symbol":"AMZN"}""", json.encode())
        assertEquals(200, response.statusCode())
        testContext.completeNow()
      })
  }

  @Test
  fun returns_quotes_for_asset(vertx: Vertx, testContext: VertxTestContext) {
    WebClient
      .create(vertx, WebClientOptions().setDefaultPort(8888))
      .get("/assets/AMZN/quotes")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        println(json)
        assertEquals("AMZN", json.getJsonObject("asset").getString("symbol"))
        assertEquals(200, response.statusCode())
        testContext.completeNow()
      })
  }
}
