package sample.pingpong

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import sample.pingpong.PingActor.Ping

class PongActor extends Actor with ActorLogging {

  import PongActor._

  def receive = {
    case Ping => println(s"${self.path} received Ping")
      sender() ! Pong
  }
}

object PongActor {

  case object Pong

  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val default = ConfigFactory.load()
    val port = default.getString("clustering.port")
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load("pingpong"))

    val system = ActorSystem(default.getString("clustering.cluster.name"), config)
    system.actorOf(Props[PongActor], name = "pongActor")
  }
}