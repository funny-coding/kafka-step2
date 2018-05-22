package xyz.funnycoding

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import xyz.funnycoding.config.Settings._
import xyz.funnycoding.models.Log

object Producer extends App {
  val  props = new Properties()
  props.put("bootstrap.servers", kafkaServer)
  props.put("key.serializer", keySerializer)
  props.put("value.serializer", valueSerializer)
  val producer = new KafkaProducer[String, String](props)

  (0 to 10000).map( i => {
    val record = new ProducerRecord[String, String](topicInputName, "line-" + i, new Log().generate)
    producer.send(record)
  }
  )
  producer.close()
}
