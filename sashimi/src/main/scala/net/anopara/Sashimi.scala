package net.anopara

import akka.actor.ActorSystem
import colossus.IOSystem
import colossus.core.{InitContext, ServerContext}
import colossus.protocols.http.Http
import colossus.protocols.http.HttpMethod._
import colossus.protocols.http.UrlParsing._
import colossus.protocols.http.{HttpServer, Initializer, RequestHandler}
import colossus.service.Callback
import colossus.service.GenRequestHandler.PartialHandler
import net.anopara.model.WpRepository

import scala.Int

object Sashimi {

  implicit val actorSystem = ActorSystem()
  implicit val ioSystem    = IOSystem()

  def start() = {
    HttpServer.start("sashimi", 9001) { context =>
      new HelloInitializer(context)
    }
  }
}

class HelloInitializer(context: InitContext) extends Initializer(context) {
  override def onConnect = context => new HelloRequestHandler(context)
}

class HelloRequestHandler(context: ServerContext) extends RequestHandler(context) {
  override def handle: PartialHandler[Http] = {
    case request @ Get on Root / "hello" => {
      Callback.successful(request.ok("Hello World!"))
    }
    case request @ Get on Root / Integer(year) / Integer(month) / Integer(day) / postName => {
      Callback.successful(request.ok(WpRepository.getPost(year, month, day, postName).toString))
    }
  }
}