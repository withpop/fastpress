package net.anopara

import akka.actor.ActorSystem
import colossus.IOSystem
import colossus.core.{InitContext, ServerContext}
import colossus.protocols.http.{Http, HttpHeader, HttpHeaders, HttpServer, Initializer, RequestHandler}
import colossus.protocols.http.HttpMethod._
import colossus.protocols.http.UrlParsing._
import colossus.service.Callback
import colossus.service.GenRequestHandler.PartialHandler
import net.anopara.model.SashimiSettings
import net.anopara.model.db.WpRepository

import scala.Int

object Sashimi {

  implicit val actorSystem = ActorSystem()
  implicit val ioSystem    = IOSystem()

  def start(settings: SashimiSettings) = {
    HttpServer.start("sashimi", settings.port) { context =>
      new SashimiInitializer(context, settings)
    }
  }
}

class SashimiInitializer(context: InitContext, settings: SashimiSettings) extends Initializer(context) {
  override def onConnect = context => new SashimiRequestHandler(context, settings)
}

class SashimiRequestHandler(context: ServerContext, settings: SashimiSettings) extends RequestHandler(context) {
  override def handle: PartialHandler[Http] = {
    case request @ Get on Root / "hello" => {
      Callback.successful(request.ok("Hello World!"))
    }
    case request @ Get on Root / Integer(year) / Integer(month) / Integer(day) / postName => {
      val result = settings.pageTemplate(WpRepository.getPost(year, month, day, postName))
      Callback.successful(request.ok(result, HttpHeaders(HttpHeader("Content-Type", "text/html"))))
    }
  }
}