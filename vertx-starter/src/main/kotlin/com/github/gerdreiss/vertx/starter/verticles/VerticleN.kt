package com.github.gerdreiss.vertx.starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VerticleN : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(VerticleN::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    val className = javaClass.name
    val threadName = Thread.currentThread()
    LOG.debug("Start $className on $threadName with config ${config()}")
    startPromise?.complete()
  }
}
