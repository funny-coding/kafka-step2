package xyz.funnycoding

import java.util.Properties
import java.util.regex.Pattern

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}
import xyz.funnycoding.config.Settings._

object Stream extends App {

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-step2")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer)
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)

  val conf: StreamsConfig = new StreamsConfig(props)
  val builder: StreamsBuilder = new StreamsBuilder

  val inputStream: KStream[String, String] = builder.stream(topicInputName)
  val records: KStream[String, (String, String, String)] = inputStream.mapValues {
    record: String => {
      val value = Pattern.compile(" ").split(record.toString)
      (value(0),
        value(1) match {
          case "[info]" => "info"
          case "[warn]" => "warn"
          case "[error]" => "error"
          case _ => "other"
        },
        record.toString)
    }
  }
  def createFilteredKStream(records: KStream[String, (String, String, String)], level: String): KStream[String, (String, String, String)] = {
    records.filter((key: String, value: (String, String, String)) => value._2 == level)
  }

  def createMappedKStream(records: KStream[String, (String, String, String)]): KStream[String, String] = {
    records.map[String, String] {
      (key: String, value: (String, String, String)) => {
        new KeyValue(value._1, value._2)
      }
    }
  }

  def createGroupedKStream(records: KStream[String, String]): KGroupedStream[String, String] = {
    records.groupBy((key: String, value: String) => key)
  }

  def countByDate(groupedStream: KGroupedStream[String, String]): KStream[String,  Int] = {
    groupedStream.aggregate(() => Map(), new Aggregator[String, String,Map[String, Int]] {
      override def apply(aggKey: String, value: String,
                         aggregate: Map[String, Int]): Map[String, Int] = {
        aggregate.keys.toList.contains(aggKey) match {
          case false => aggregate ++ Map (aggKey -> 1)
          case true => aggregate.map {
            case (key,value) =>
              if (key == aggKey)
                (key, aggregate(key) + 1)
              else
                (key, aggregate(key))
          }
        }
      }
    }).toStream.map[String, Int]{
      (key: String, value: Map[String, Int]) => {
        new KeyValue(key, value.values.head)
      }
    }
  }

  val errorStream: KStream[String,  Int] = countByDate(createGroupedKStream(createMappedKStream(createFilteredKStream(records, "error"))))
  val infoStream: KStream[String,  Int] = countByDate(createGroupedKStream(createMappedKStream(createFilteredKStream(records, "info"))))
  val warnStream: KStream[String,  Int] = countByDate(createGroupedKStream(createMappedKStream(createFilteredKStream(records, "warn"))))
  val otherStream: KStream[String,  Int] = countByDate(createGroupedKStream(createMappedKStream(createFilteredKStream(records, "other"))))


  errorStream.to(topicErrorName)
  warnStream.to(topicWarnName)
  infoStream.to(topicInfoName)
  otherStream.to(topicOtherName)
  
  val streams = new KafkaStreams(builder.build(), conf)
  streams.start()
}
