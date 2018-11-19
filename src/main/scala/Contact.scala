import java.util.UUID

import play.api.libs.json.Json

import scala.util.Random

object Contact {
  implicit val format = Json.format[Contact]

  def random = {
    Contact(
      UUID.randomUUID().toString,
      Random.alphanumeric.take(10).mkString,
      Random.alphanumeric.take(5).mkString,
      Random.alphanumeric.take(5).mkString
    )
  }
}

case class Contact(id: String, name: String, firstName: String, lastName: String)
