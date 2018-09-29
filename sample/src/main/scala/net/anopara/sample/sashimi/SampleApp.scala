package net.anopara.sample.sashimi

import akka.http.scaladsl.Http
import io.getquill.JdbcContextConfig
import io.getquill.util.LoadConfig
import net.anopara.Sashimi
import net.anopara.auth.BCryptEncoder
import net.anopara.model.SashimiSettings
import net.anopara.sample.sashimi.templates.html._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

object SampleApp extends App {
//  Sashimi.start(new SashimiSettings(
//    port = 9001,
//    pageTemplate = index.apply(_).body,
//    notFoundTemplate = s => s"not found: $s"
//  ))

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  //implicit val executionContext = system.dispatcher
  implicit val executionContext = system.dispatchers.lookup("my-blocking-dispatcher")

  val routes = pathSingleSlash {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
    }
  }

  routes ~
    path("aaa") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }


  Http().bindAndHandle(routes, "localhost", 9001)

}
