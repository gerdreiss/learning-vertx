package com.github.gerdreiss.vertx.playground.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object Deserializer : JsonDeserializer<Person>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Person {
    val node = p.readValueAsTree<JsonNode>()

    val id = node.get("id").asInt()
    val name = node.get("name").asText()
    val lovesVertx = node.get("lovesVertx").asBoolean()

    return Person(id, name, lovesVertx)
  }
}

@JsonDeserialize(using = Deserializer::class)
data class Person(val id: Int, val name: String, val lovesVertx: Boolean = true)

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
    val jsonMap = mapOf(
      "id" to 1,
      "name" to "Alice",
      "loves_vertx" to true
    )
    val asJsonObject = JsonObject(jsonMap)
    assertEquals(jsonMap, asJsonObject.map)
    assertEquals(1, asJsonObject.getInteger("id"))
    assertEquals("Alice", asJsonObject.getString("name"))
    assertEquals("Alice", asJsonObject["name"])
    assertEquals(true, asJsonObject.getBoolean("loves_vertx"))
    assertEquals(true, asJsonObject["loves_vertx"])
    assertEquals("true", asJsonObject.getString("loves_vertx"))
  }


  @Test
  fun jsonArrayCanBeMapped() {
    val arr = JsonArray(
      mutableListOf(
        JsonObject(mapOf("id" to 1)),
        JsonObject(mapOf("id" to 2)),
        JsonObject(mapOf("id" to 3))
      )
    )
    assertEquals("""[{"id":1},{"id":2},{"id":3}]""", arr.encode())
  }

  @Test
  fun canMapJavaObjects() {
    val person = Person(1, "Bob")
    val jsonObj = JsonObject.mapFrom(person)

    assertEquals(person.id, jsonObj.getInteger("id"))
    assertEquals(person.name, jsonObj.getString("name"))
    assertEquals(person.lovesVertx, jsonObj.getBoolean("lovesVertx"))

    val mapped = jsonObj.mapTo(Person::class.java)

    assertEquals(person.id, mapped.id)
    assertEquals(person.name, mapped.name)
    assertEquals(person.lovesVertx, mapped.lovesVertx)
  }
}
