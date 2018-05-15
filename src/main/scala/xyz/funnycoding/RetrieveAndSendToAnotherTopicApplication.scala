package xyz.funnycoding

import java.util.Properties

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import xyz.funnycoding.config.Settings._

object RetrieveAndSendToAnotherTopicApplication extends App {

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-step2")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer)
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)

  val conf: StreamsConfig = new StreamsConfig(props)
  val builder: StreamsBuilder = new StreamsBuilder

  val sourceStream: KStream[String, String] = builder.stream("sourceTopic")
  sourceStream.to("endTopic")

  val streams = new KafkaStreams(builder.build(), conf)
  streams.start()
}
