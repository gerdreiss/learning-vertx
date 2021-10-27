package com.github.gerdreiss.vertx.playground.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Sender : AbstractVerticle() {
  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    vertx.setPeriodic(1000) { id ->
      vertx.eventBus().send(Sender::class.qualifiedName, "Sending a message from $id...")
    }
  }
}

class Receiver : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(Receiver::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    vertx.eventBus().consumer<String>(Sender::class.qualifiedName) { message ->
      LOG.debug("Received ${message.body()}")
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(Sender())
  vertx.deployVerticle(Receiver())
}
