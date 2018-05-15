package xyz.funnycoding.config

import com.typesafe.config.{Config, ConfigFactory}

object Settings {

  lazy val config: Config = ConfigFactory.load()
  val kafkaServer: String = config.getString("conf.bootstrap.servers")
  val keyDeserializer: String = config.getString("conf.key.deserializer")
  val valueDeserializer: String = config.getString("conf.value.deserializer")
  val keySerializer: String = config.getString("conf.key.serializer")
  val valueSerializer: String = config.getString("conf.value.serializer")
  val consumerGroupId: String = config.getString("conf.group.id")
  val topicName: String = config.getString("conf.topic.name")
}
