package com.github.gerdreiss.vertx.playground.workers

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorkerVerticle : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(WorkerVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    LOG.debug("Deployed as worker verticle")
    startPromise?.complete()
    Thread.sleep(5000)
    LOG.debug("Worker operation done.")
  }

}
