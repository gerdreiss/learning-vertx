package com.github.gerdreiss.vertx_starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleB : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>?) {
    LOG.debug("Start ${javaClass.name}")
    startPromise?.complete()
  }
}
