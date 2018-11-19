import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.ImplicitConversions._

import play.api.libs.json._

/**
  * Transform Orders to XeroInvoice
  */
object XeroInvoiceETL extends App {

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "xero-invoice-etl")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092")
    p
  }

  val builder = new StreamsBuilder
  val orderStream = builder.stream[String, String]("orders").map { (id, value) =>
    println(s"Processing Order: $id")

    val order = Json.parse(value)
    val contact = Json.toJson(Contact.random) // enrich the orders by inserting Contact
    val lineItem = Json.obj(
      "LineItemID" -> (order \ "itemid").get,
      "Quantity" -> (order \ "orderunits").get
    )
    val xeroInvoice = Json.obj(
      "Type" -> "ACCREC",
      "Date" -> (order \ "ordertime").get,
      "Status" -> "AUTHORISED",
      "Contact" -> contact,
      "LineItems" -> JsArray(Seq(lineItem))
    )

    (id, Json.stringify(xeroInvoice))
  }
  orderStream.to("xero-invoices")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), config)

  streams.start()

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

}
