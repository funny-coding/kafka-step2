package xyz.funnycoding.models
import java.sql.Timestamp
import java.text.SimpleDateFormat

import java.util.Random

class Log {
  val timestamp:String = new SimpleDateFormat("dd/MM/yyyy").format(generateTimestamp)
  val level :String = generateLevel

  def generate:String = s"${timestamp} [$level] ---- more informations"

  def generateTimestamp:Timestamp={
    val offset: Long = System.currentTimeMillis()
    new Timestamp(offset + new Random().nextInt())
  }
  def generateLevel:String={
    val levels:List[String] = List("degug", "info","warn", "error")
    val rand = new Random()
    val random_index = rand.nextInt(levels.length)
    levels(random_index)
  }

}
