package net.anopara

import akka.actor.ActorSystem
import colossus.IOSystem
import colossus.core.{InitContext, ServerContext}
import colossus.protocols.http.{Http, HttpHeader, HttpHeaders, HttpServer, Initializer, RequestHandler}
import colossus.protocols.http.HttpMethod._
import colossus.protocols.http.UrlParsing._
import colossus.service.Callback
import colossus.service.GenRequestHandler.PartialHandler
import net.anopara.model.{Renderer, SashimiSettings}
import net.anopara.model.db.WpRepository

import scala.Int

object Sashimi {

  implicit val actorSystem = ActorSystem()
  implicit val ioSystem    = IOSystem()

  def start(settings: SashimiSettings) = {
    val repository = new WpRepository(settings.postCacheTime)
    HttpServer.start("sashimi", settings.port) { context =>
      new SashimiInitializer(context, settings, repository)
    }
  }
}

class SashimiInitializer(context: InitContext, settings: SashimiSettings, wpRepository: WpRepository) extends Initializer(context) {
  override def onConnect = context => new SashimiRequestHandler(context, settings, wpRepository: WpRepository)
}

class SashimiRequestHandler(context: ServerContext, settings: SashimiSettings, repository: WpRepository) extends RequestHandler(context) {

  val renderer = new Renderer(settings.pageTemplate)

  override def handle: PartialHandler[Http] = {
    case request @ Get on Root / "hello" => {
      Callback.successful(request.ok("Hello World!"))
    }

    case request @ Get on Root / Integer(year) / Integer(month) / Integer(day) / postName => {
      repository.getPost(year, month, day, postName) match {
        case Some(p) =>
          Callback.successful(request.ok(renderer.renderPage(p), HttpHeaders(HttpHeader("Content-Type", "text/html"))))
        case None =>
          Callback.successful(request.notFound("Not Found")) // TODO need template for 404
      }
    }

    case request @ Get on Root / pageName => {
      repository.getPage(pageName) match {
        case Some(p) =>
          Callback.successful(request.ok(renderer.renderPage(p), HttpHeaders(HttpHeader("Content-Type", "text/html"))))
        case None =>
          Callback.successful(request.notFound("Not Found")) // TODO need template for 404
      }
    }
  }
}