import java.time.Duration

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

object XeroConsumer extends App {

  import java.util.Properties

  val TOPIC = "xero-invoices"
  val props = new Properties()

  props.put("bootstrap.servers", "localhost:29092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")
  props.put("max.poll.records", "50")

  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(List(TOPIC).asJavaCollection)

  while(true) {
    val records = consumer.poll(Duration.ofMinutes(1))
    val invoices = records.iterator().asScala.map(_.value).mkString(",")
    println(s"Pushing ${records.count()} Invoices to Xero")
    // calling Xero API to posting $invoices

    consumer.commitSync
    Thread.sleep(100)
  }

}