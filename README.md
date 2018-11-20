# kafka-streams-test
A prototype demonstrates stream processing by using Kafka.
![Architecture](architecture.jpg)

## Prerequisite
* [Docker Compose](https://docs.docker.com/compose/install/)
  * Make sure Docker memory resource is allocated minimally at **8 GB**.
* [Scala](https://www.scala-lang.org/download/)
* [SBT](https://www.scala-sbt.org/download.html)

## Setup
1. Setup Confluent Platform Using Docker
```bash
docker-compose up -d --build
```
2. Kafka Topics

 2.1 Create Ingestion Topic `orders`
```bash
docker-compose exec broker kafka-topics --create \
--zookeeper zookeeper:2181 \
--replication-factor 1 --partitions 1 \
--topic orders
```

 2.2 Create Consumer Topic `xero-invoices`
```bash
docker-compose exec broker kafka-topics --create \
--zookeeper zookeeper:2181 \
--replication-factor 1 --partitions 1 \
--topic xero-invoices
```

3. Install a Kafka Connector and Generate Sample Data
```bash
curl -X POST -H "Content-Type: application/json" \
--data @connector_orders_cos.config \
http://localhost:8083/connectors
```

4. Create Streams using KSQL

 Connecting to KSQL
```bash
docker-compose exec ksql-cli ksql http://ksql-server:8088
```

 4.1 Create Stream `orders`
```sql
CREATE STREAM orders \
(ordertime BIGINT, orderid VARCHAR, itemid VARCHAR, orderunits DOUBLE, address map<VARCHAR, VARCHAR>) \
WITH (KAFKA_TOPIC='orders', VALUE_FORMAT='JSON', KEY='orderid', TIMESTAMP='ordertime');
```

 4.2 Create Stream `xero-invoices`
 ```sql
CREATE STREAM xero_invoices \
(Type VARCHAR, Date BIGINT, Status VARCHAR, Contact map<VARCHAR, VARCHAR>, LineItems array<map<VARCHAR, VARCHAR>>) \
WITH (KAFKA_TOPIC='xero-invoices', VALUE_FORMAT='JSON', TIMESTAMP='Date');
```

5. Run [Order to XeroInvoice Script ](src/main/scala/XeroInvoiceETL.scala)
```bash
sbt "runMain XeroInvoiceETL"
```

6. Run [Xero Consumer](src/main/scala/XeroConsumer.scala)
```bash
sbt "runMain XeroConsumer"
```

## Results
![Kafka Demo Results](kafka-demo.gif)

## Reference
* Confluent Quick Start (https://docs.confluent.io/current/quickstart/cos-docker-quickstart.html)
* Stream Processing Framework comparison (https://www.linkedin.com/pulse/spark-streaming-vs-flink-storm-kafka-streams-samza-choose-prakash)
* Scaling in Kafka Streams (https://www.confluent.io/blog/elastic-scaling-in-kafka-streams/)

----
## Things to Consider

### Alternative
* Apache Flink (https://www.confluent.io/blog/apache-flink-apache-kafka-streams-comparison-guideline-users/)

### Programming Language
* Any Langauge can write Producer & Consumer (Offical Clients or REST Proxy)
* Streams API only available on JVM Langauage

### Event Message Schema
* Support any serialization formats
* KSQL only supports plain text, JSON, Avro Schema

### Monitoring
* Kafka Monitoring
* Stream Processor Monitoring (AppSignal don't have Java client)

### Scaling
* Stream Processor & Consumer
* Partitioning Kafka Topics

### Data Archives
* Default Retention Period = 7 days (can be forever)
* Event Sourcing
* Replay topic messages
