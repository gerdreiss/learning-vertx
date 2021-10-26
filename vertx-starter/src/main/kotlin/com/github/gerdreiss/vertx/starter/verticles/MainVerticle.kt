package com.github.gerdreiss.vertx.starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(VerticleN::class.java)
  }

  override fun start() {
    LOG.debug("Start ${javaClass.name}")
    vertx.deployVerticle(VerticleA())
    vertx.deployVerticle(VerticleB())
    vertx.deployVerticle(
      VerticleN::class.qualifiedName,
      DeploymentOptions()
        .setInstances(4)
        .setConfig(
          JsonObject()
            .put("id", java.util.UUID.randomUUID().toString())
            .put("name", VerticleN::class.simpleName)
        )
    )
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
