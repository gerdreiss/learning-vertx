package com.github.gerdreiss.vertx.starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VerticleAA : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(VerticleN::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    LOG.debug("Start ${javaClass.name}")
    startPromise?.complete()
  }

  override fun stop(stopPromise: Promise<Void>?) {
    LOG.debug("Stop ${javaClass.name}")
    stopPromise?.complete()
  }
}
