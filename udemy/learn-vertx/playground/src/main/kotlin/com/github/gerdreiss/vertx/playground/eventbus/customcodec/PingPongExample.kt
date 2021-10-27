package com.github.gerdreiss.vertx.playground.eventbus.customcodec

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.MessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random


data class Ping(val message: String, val enabled: Boolean = true)
data class Pong(val hash: Int)


class LocalMessageCodec<T>(private val type: Class<T>) : MessageCodec<T, T> {
  override fun encodeToWire(buffer: Buffer?, s: T) {
    TODO("Not yet implemented")
  }

  override fun decodeFromWire(pos: Int, buffer: Buffer?): T {
    TODO("Not yet implemented")
  }

  override fun transform(obj: T): T = obj
  override fun name(): String = type.name
  override fun systemCodecID(): Byte = -1
}


class PingVerticle : AbstractVerticle() {

  companion object {
    const val ADDRESS: String = "ping.pong.exchange"
    val LOG: Logger = LoggerFactory.getLogger(PingVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    val message = Ping(Random(1L).nextLong().toString())
    LOG.debug("Sending $message")
    vertx
      .eventBus()
      .registerDefaultCodec(Ping::class.java, LocalMessageCodec(Ping::class.java))
      .request<Pong>(ADDRESS, message) { result ->
        if (result.succeeded()) {
          LOG.debug("Response: ${result.result().body()}")
        } else {
          LOG.error("Failed: ${result.cause()}")
        }
      }
    startPromise?.complete()
  }
}

class PongVerticle : AbstractVerticle() {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(PongVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>?) {
    vertx
      .eventBus()
      .registerDefaultCodec(Pong::class.java, LocalMessageCodec(Pong::class.java))
      .consumer<Ping>(PingVerticle.ADDRESS) { message ->
        LOG.debug("Received ${message.body()}")
        message.reply(
          Pong(message.body().message.hashCode()),
          DeliveryOptions().setSendTimeout(10000)
        )
      }
      .exceptionHandler { error ->
        LOG.error("Error: $error")
      }
    startPromise?.complete()
  }
}


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(PingVerticle()) { ar ->
    if (ar.failed()) {
      println("PingVerticle deployment failed with ${ar.cause()}")
    }
  }
  vertx.deployVerticle(PongVerticle()) { ar ->
    if (ar.failed()) {
      println("PongVerticle deployment failed with ${ar.cause()}")
    }
  }
}
