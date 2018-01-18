package net.anopara.handler

import io.undertow.Handlers
import io.undertow.server.handlers.form._
import io.undertow.server.session.{Session, SessionAttachmentHandler}
import io.undertow.server.{HttpHandler, HttpServerExchange, RoutingHandler}
import io.undertow.util.{Headers, Sessions, StatusCodes}
import net.anopara.model.SashimiSettings
import net.anopara.model.db.{AdminPageDataSet, Repository, User}
import net.anopara.model.service.AuthService
import net.anopara.sashimi.html._

class AdminPageHandler(
  settings: SashimiSettings,
  auth: AuthService,
  repo: Repository
) extends HandlerBase {

  private[this] val indexPageHandler: HttpHandler = authenticated{ (ex, user) =>
    setResponse(ex, admintop(new AdminPageDataSet(user, settings)).body)
  }

  private[this] val loginPageHandler: HttpHandler = ex => {
    setResponse(ex, login(new AdminPageDataSet(User("dummy", "dummy", "dummy", "dummy"), settings)).body)
  }

  private[this] val logoutHandler: HttpHandler = ex => {
    Sessions.getSession(ex) match {
      case x: Session =>
        x.invalidate(ex)
      case _ =>
    }

    redirect(ex, settings.loginUrl)
  }

  private[this] val loginFormHandler: HttpHandler = ex => {
    val session = Sessions.getOrCreateSession(ex)
    val form = ex.getAttachment(FormDataParser.FORM_DATA)
    val maybeUser = for {
      u <- Option(form.getFirst("userId")).map(_.getValue)
      p <- Option(form.getFirst("password")).map(_.getValue)
      user <- auth.authenticate(u, p, session.getId)
    } yield user
    val redirectUrl = Option(form.getFirst("redirect"))
      .map(_.getValue)
      .getOrElse(settings.getUrl("/admin/"))

    maybeUser match {
      case Some(x) =>
        redirect(ex, redirectUrl)
      case None =>
        redirect(ex, settings.loginUrl(redirectUrl, "not_match"))
    }
  }

  val handler: HttpHandler = {
    val handler = Handlers.routing()
      .get("/", indexPageHandler)
      .get("/login", loginPageHandler)
      .get("/logout", logoutHandler)
      .post("/login", formParsed(loginFormHandler))

    import io.undertow.server.session.InMemorySessionManager
    import io.undertow.server.session.SessionCookieConfig

    val manager = new InMemorySessionManager("SESSION_MANAGER")
    val config = new SessionCookieConfig

    new SessionAttachmentHandler(manager, config).setNext(handler)
  }

  private def formParsed(handler: HttpHandler): HttpHandler = {
    new EagerFormParsingHandler(
      FormParserFactory.builder()
        .addParsers(new FormEncodedDataDefinition())
        .build()
    ).setNext(handler)
  }

  private def authenticated(block: (HttpServerExchange, User) => Unit): HttpHandler = ex => {
    val maybeUser = for {
      s <- Option(Sessions.getOrCreateSession(ex))
      id <- Option(s.getId)
      u <- auth.getUserFromSession(id)
    } yield u

    maybeUser match {
      case None =>
        val url = settings.loginUrl(ex.getRequestPath)
        redirect(ex, url)

      case Some(x) =>
        block(ex, x)
    }
  }
}
