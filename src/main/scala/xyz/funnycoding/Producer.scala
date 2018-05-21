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
  var i:Int = 0
  while(i<10000){
    i += 1
    val record = new ProducerRecord[String, String](topicInputName,"line-"+i , new Log().generate)
    producer.send(record)
  }
  producer.close()
}
