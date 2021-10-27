package com.github.gerdreiss.vertx.playground.eventbus

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Publisher : AbstractVerticle() {
  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    vertx.setPeriodic(3000) {
      vertx.eventBus().publish(Publisher::class.qualifiedName, "A message for everyone!")
    }
  }
}

class Subscriber1 : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(Subscriber1::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    vertx.eventBus().consumer<String>(Publisher::class.qualifiedName) { message ->
      LOG.debug("Received ${message.body()}")
    }
  }
}

class Subscriber2 : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(Subscriber2::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    startPromise?.complete()
    vertx.eventBus().consumer<String>(Publisher::class.qualifiedName) { message ->
      LOG.debug("Received ${message.body()}")
    }
  }
}


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(Publisher())
  vertx.deployVerticle(Subscriber1())
  vertx.deployVerticle(Subscriber2::class.java, DeploymentOptions().setInstances(2))
}
