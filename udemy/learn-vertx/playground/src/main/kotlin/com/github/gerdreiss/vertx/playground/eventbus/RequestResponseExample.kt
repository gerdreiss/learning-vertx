package com.github.gerdreiss.vertx.playground.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestVerticle : AbstractVerticle() {

  companion object {
    const val ADDRESS: String = "my.request.address"
    val LOG: Logger = LoggerFactory.getLogger(RequestVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    val message = "Hello World!"
    LOG.debug("Sending $message")
    vertx.eventBus().request<String>(ADDRESS, message) { result ->
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
    vertx.eventBus().consumer<String>(RequestVerticle.ADDRESS) { message ->
      LOG.debug("Received ${message.body()}")
      message.reply("Received your message", DeliveryOptions().setSendTimeout(10000))
    }
  }
}


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(RequestVerticle())
  vertx.deployVerticle(ResponseVerticle())
}
