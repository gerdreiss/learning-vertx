package com.github.gerdreiss.vertx.playground.futurepromises

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExtendWith(VertxExtension::class)
class FuturePromiseExample {

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(FuturePromiseExample::class.java)
  }

  @Test
  fun promise_success(vertx: Vertx, context: VertxTestContext) {
    val promise = Promise.promise<String>()

    LOG.debug("Start")

    vertx.setTimer(500) {
      promise.complete("Success")
      LOG.debug("Success")
      context.completeNow()
    }

    LOG.debug("End")
  }

  @Test
  fun promise_failure(vertx: Vertx, context: VertxTestContext) {
    val promise = Promise.promise<String>()

    LOG.debug("Start")

    vertx.setTimer(500) {
      promise.fail(RuntimeException("Failed!"))
      LOG.debug("Failed!")
      context.completeNow()
    }

    LOG.debug("End")
  }

  @Test
  fun future_success(vertx: Vertx, context: VertxTestContext) {
    val promise = Promise.promise<String>()

    LOG.debug("Start")

    vertx.setTimer(500) {
      promise.complete("Success")
      LOG.debug("Timer done.")
      context.completeNow()
    }

    promise.future()
      .onSuccess {
        LOG.debug("Result: $it")
        context.completeNow()
      }
      .onFailure(context::failNow)

    LOG.debug("End")
  }

  @Test
  fun future_failure(vertx: Vertx, context: VertxTestContext) {
    val promise = Promise.promise<String>()

    LOG.debug("Start")

    vertx.setTimer(500) {
      promise.fail(RuntimeException("Failed!"))
      LOG.debug("Timer done.")
    }

    promise.future()
//      .onSuccess {
//        LOG.debug("Result: $it")
//        context.completeNow()
//      }
      .onSuccess(context::failNow)
      .onFailure {
        LOG.debug("Result: $it")
        context.completeNow()
      }

    LOG.debug("End")
  }

  @Test
  fun future_map(vertx: Vertx, context: VertxTestContext) {
    val promise = Promise.promise<String>()

    LOG.debug("Start")

    vertx.setTimer(500) {
      promise.complete("Success")
      LOG.debug("Timer done.")
      context.completeNow()
    }

    promise.future()
      .map {
        LOG.debug("Map String to JsonObject")
        JsonObject().put("key", it)
      }
      .map {
        JsonArray().add(it)
      }
      .onSuccess {
        LOG.debug("Result: $it of type ${it::class.java.simpleName}")
        context.completeNow()
      }
      .onFailure(context::failNow)

    LOG.debug("End")
  }

  //  @Test
  //  fun future_coordination(vertx: Vertx, context: VertxTestContext) {
  //    vertx.createHttpServer()
  //      .requestHandler { LOG.debug("$it") }
  //      .listen(10_000)
  //      .compose {
  //        LOG.info("Another task")
  //        Future.succeededFuture(it)
  //      }
  //      .compose {
  //        LOG.info("Yet another task")
  //        Future.succeededFuture(it)
  //      }
  //      .onFailure(context::failNow)
  //      .onSuccess {
  //        LOG.debug("Server started on port ${it.actualPort()}")
  //      }
  //  }

  @Test
  fun future_composition(vertx: Vertx, context: VertxTestContext) {
    val one = Promise.promise<Void>()
    val two = Promise.promise<Void>()
    val three = Promise.promise<Void>()

    val future1 = one.future()
    val future2 = two.future()
    val future3 = three.future()

    CompositeFuture
      .any(future1, future2, future3)
      .onFailure(context::failNow)
      .onSuccess {
        LOG.debug("Success")
        context.completeNow()
      }

    vertx.setTimer(500) {
      one.complete()
      two.complete()
      three.fail("Boom!")
    }
  }
}
