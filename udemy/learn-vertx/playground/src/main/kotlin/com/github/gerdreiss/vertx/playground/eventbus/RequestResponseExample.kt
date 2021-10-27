package com.github.gerdreiss.vertx.playground.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestVerticle : AbstractVerticle() {

  companion object {
    const val ADDRESS: String = "my.request.address"
    val LOG: Logger = LoggerFactory.getLogger(RequestVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    val message = JsonObject()
      .put("message", "Hello World!")
      .put("version", 1)
    LOG.debug("Sending $message")
    vertx.eventBus().request<JsonObject>(ADDRESS, message) { result ->
      LOG.debug("Response: ${result.result().body()}")
    }
  }
}

class ResponseVerticle : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(ResponseVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    vertx
      .eventBus()
      .consumer<JsonObject>(RequestVerticle.ADDRESS) { message ->
        LOG.debug("Received ${message.body()}")

        val response = message
          .body()
          .encode()
          .split("\\W+".toRegex())
          .filterNot { it.isBlank() }
          .fold(JsonArray()) { acc: JsonArray, word: String ->
            acc.add(word)
          }

        message.reply(response, DeliveryOptions().setSendTimeout(10000))
      }
  }
}


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(RequestVerticle())
  vertx.deployVerticle(ResponseVerticle())
}
