package com.github.gerdreiss.vertx_starter.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.util.*

class MainVerticle : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(javaClass)

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
            .put("id", UUID.randomUUID().toString())
            .put("name", VerticleN::class.simpleName)
        )
    )
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
