package com.github.gerdreiss.vertx_starter

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

class MainVerticle : AbstractVerticle() {

  private fun randomFile(): String {
    val files = Files
      .list(Path.of(System.getProperty("user.home")))
      .filter {
        it.isRegularFile(LinkOption.NOFOLLOW_LINKS)
      }
      .toList()
    return files[Random().nextInt(files.size)].absolutePathString()
  }

  override fun start(startPromise: Promise<Void>) {
    vertx
      .createHttpServer()
      .requestHandler { req ->
        vertx.fileSystem()
          .readFile(randomFile())
          .onComplete { asyncResult ->
            if (asyncResult.succeeded()) {
              req.response()
                .putHeader("content-type", "text/plain")
                .end(asyncResult.result().toString())
            } else {
              req.response()
                .putHeader("content-type", "text/plain")
                .end("Well, that didn't work out...")
            }
          }
      }
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
