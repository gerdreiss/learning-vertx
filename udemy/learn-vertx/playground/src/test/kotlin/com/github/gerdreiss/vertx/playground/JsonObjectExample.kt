package com.github.gerdreiss.vertx.playground

import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonObjectExample {

  @Test
  fun jsonObjectCanBeMapped() {
    val jsonObj = JsonObject()
      .put("id", 1)
      .put("name", "Bob")
      .put("loves_vertx", true)

    val encoded = jsonObj.encode()
    assertEquals("""{"id":1,"name":"Bob","loves_vertx":true}""", encoded)

    val decoded = JsonObject(encoded)
    assertEquals(jsonObj, decoded)
  }

  @Test
  fun jsonObjectCanBeCreatedFromMap() {
    val jsonMap = mapOf("id" to 1, "name" to "Alice", "loves_vertx" to true)
    val asJsonObject = JsonObject(jsonMap)
    assertEquals(jsonMap, asJsonObject.map)
    assertEquals(1, asJsonObject.getInteger("id"))
    assertEquals("Alice", asJsonObject.getString("name"))
    assertEquals("Alice", asJsonObject["name"])
    assertEquals(true, asJsonObject.getBoolean("loves_vertx"))
    assertEquals(true, asJsonObject["loves_vertx"])
    assertEquals("true", asJsonObject.getString("loves_vertx"))
  }

}
