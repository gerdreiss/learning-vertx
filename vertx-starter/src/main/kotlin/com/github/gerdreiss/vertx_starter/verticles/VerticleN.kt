package com.github.gerdreiss.vertx_starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleN : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>?) {
    val className = javaClass.name
    val threadName = Thread.currentThread()
    LOG.debug("Start $className on $threadName with config ${config()}")
    startPromise?.complete()
  }
}
