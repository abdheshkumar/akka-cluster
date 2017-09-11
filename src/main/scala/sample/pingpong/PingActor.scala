package sample.pingpong

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster
import akka.routing.FromConfig
import akka.actor.ReceiveTimeout
import PongActor.Pong
import scala.util.Try
import scala.concurrent.Await

class PingActor() extends Actor with ActorLogging {

  import PingActor._

  val pong = context.actorOf(FromConfig.props(Props[PongActor]),
    name = "pongActorRouter")
  implicit val executionContext = context.dispatcher

  override def preStart(): Unit = {
    sendJobs
    context.setReceiveTimeout(10.seconds)
  }

  def receive = {
    case Pong => println(s"${self.path} received Pong")
    case ReceiveTimeout => log.info("Timeout")
  }

  def sendJobs: Unit = {
    context.system.scheduler.schedule(1.second, 5.second)(pong ! Ping)
  }
}

object PingActor {

  case object Ping

  def main(args: Array[String]): Unit = {
    val default = ConfigFactory.load()
    val port = default.getString("clustering.port")
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
      .withFallback(ConfigFactory.load("pingpong"))

    val system = ActorSystem(default.getString("clustering.cluster.name"), config)

    Cluster(system) registerOnMemberUp {
      system.actorOf(Props[PingActor], name = "pingActor")
    }

    Cluster(system).registerOnMemberRemoved {
      system.registerOnTermination(System.exit(0))
      system.terminate()

      // In case ActorSystem shutdown takes longer than 10 seconds,
      // exit the JVM forcefully anyway.
      // We must spawn a separate thread to not block current thread,
      // since that would have blocked the shutdown of the ActorSystem.
      new Thread {
        override def run(): Unit = {
          if (Try(Await.ready(system.whenTerminated, 10.seconds)).isFailure)
            System.exit(-1)
        }
      }.start()
    }
  }
}
