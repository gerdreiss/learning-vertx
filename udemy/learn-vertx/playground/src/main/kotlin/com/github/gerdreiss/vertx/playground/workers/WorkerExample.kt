package com.github.gerdreiss.vertx.playground.workers

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorkerExample : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(WorkerExample::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {

    vertx.deployVerticle(
      WorkerVerticle(),
      DeploymentOptions()
        .setWorker(true)
        .setWorkerPoolSize(1)
        .setWorkerPoolName("worker-verticle-example")
    )

    startPromise?.complete()
    executeBlocking()
  }

  private fun executeBlocking() {
    vertx.executeBlocking<Unit>(
      { event ->
        LOG.debug("Executing blocking code.")
        Thread.sleep(5000)
        event.complete()
      },
      {
        it.map {
          LOG.debug("Blocking call done.")
        }.otherwise { error ->
          LOG.debug("Blocking call failed due to: $error")
        }
      }
    )
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(WorkerExample())
}
