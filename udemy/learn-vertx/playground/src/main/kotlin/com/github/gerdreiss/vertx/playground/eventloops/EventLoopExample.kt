package com.github.gerdreiss.vertx.playground.eventloops

import io.vertx.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class EventLoopExample : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(EventLoopExample::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    LOG.debug("Start $javaClass")
    startPromise?.complete()
    // never do this inside a verticle!
    Thread.sleep(5000)
  }

}

fun main() {
  val vertx = Vertx.vertx(
    VertxOptions()
      .setMaxEventLoopExecuteTime(500)
      .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
      .setBlockedThreadCheckInterval(1)
      .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
      // this setting does not make any sense
      // since we want to start 4 instances of the verticle (see here)
      .setEventLoopPoolSize(2)                                    //
  )                                                               //
  vertx.deployVerticle(                                           //
    EventLoopExample::class.qualifiedName,                        //
    DeploymentOptions().setInstances(4) // <<=====================//
  )
}
