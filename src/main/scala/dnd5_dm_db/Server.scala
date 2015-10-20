package dnd5_dm_db

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

import scala.concurrent.duration._
import scala.util.Properties

object Settings {

  private [this] var _port = Properties.envOrElse("dnd5_dm_db_port", "8080").toInt

  def port : Int = _port

  private [this] var _resourcesDir = Properties.envOrElse("dnd5_dm_db_resources_dir", ".")

  def resourcesDir : String = _resourcesDir

  private [this] var _genAllOutDir = Properties.envOrElse("dnd5_dm_db_gen_all_out_dir", "./out")

  def genAllOutDir : String = _genAllOutDir

  def init(args : Array[String]) : Unit = {
    args foreach { a =>
      val arr = a.split("=")
      val key = arr(0)
      val value = arr(1)

      key match {
        case "port" =>
          _port = value.toInt

        case "resourcesDir" =>
          _resourcesDir = value

        case "genAllOutDir" =>
          _genAllOutDir = value

      }
    }
  }

}

object Server extends App {

  Settings.init(args)

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(ServiceActor.props(Settings.resourcesDir), "demo-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = Settings.port)
}
