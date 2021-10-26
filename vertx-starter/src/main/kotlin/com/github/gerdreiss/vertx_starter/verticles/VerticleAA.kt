package com.github.gerdreiss.vertx_starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleAA : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>?) {
    LOG.debug("Start ${javaClass.name}")
    startPromise?.complete()
  }

  override fun stop(stopPromise: Promise<Void>?) {
    LOG.debug("Stop ${javaClass.name}")
    stopPromise?.complete()
  }
}
