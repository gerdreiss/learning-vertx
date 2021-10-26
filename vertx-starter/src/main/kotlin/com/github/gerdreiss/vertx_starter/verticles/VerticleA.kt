package com.github.gerdreiss.vertx_starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VerticleA : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>?) {
    LOG.debug("Start ${javaClass.name}")
    vertx.deployVerticle(VerticleAA()) { whenDeployed ->
      LOG.debug("Deployed ${VerticleAA::class.qualifiedName}")
      vertx.undeploy(whenDeployed.result())
    }
    vertx.deployVerticle(VerticleAB()) {
      LOG.debug("Deployed ${VerticleAB::class.qualifiedName}")
      // do not undeploy
    }
    startPromise?.complete()
  }
}
