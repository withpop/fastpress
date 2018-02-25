package net.anopara.handler

import java.time.LocalDateTime

import io.undertow.Handlers
import io.undertow.server.handlers.form._
import io.undertow.server.session.{Session, SessionAttachmentHandler}
import io.undertow.server.{HttpHandler, HttpServerExchange, RoutingHandler}
import io.undertow.util.{Headers, PathTemplateMatch, Sessions, StatusCodes}
import net.anopara.model.SashimiSettings
import net.anopara.model.console.SavingData
import net.anopara.model.db._
import net.anopara.model.service.AuthService
import net.anopara.sashimi.html._
import play.twirl.api.{Html, HtmlFormat}

class AdminPageHandler(
  settings: SashimiSettings,
  auth: AuthService,
  repo: Repository
) extends HandlerBase {

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

  private[this] val newPostHandler: HttpHandler = authenticated{ (ex, user) =>
    val post = Post(
      id = 0,
      title = "",
      content = "",
      pathName = "",
      status = "draft",
      author = user.userName,
      postType = "post",
      attribute = "",
      postedAt = LocalDateTime.now(),
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    val render = new RenderDataSet(post, List.empty, List.empty, None)
    setResponse(ex, newPost(new AdminPageDataSet(user, settings), render, repo.getTags, repo.getCategories).body)
  }

  private[this] val editPagePostHandler: HttpHandler = authenticated{ (ex, user) =>
    val id = ex.getPathParameters.get("postId").peekFirst().toInt
    repo.getRenderDataNonCache(id) match {
      case None =>
        setResponse(ex, "no such post", StatusCodes.NOT_FOUND)
      case Some(render) =>
        setResponse(ex, newPost(new AdminPageDataSet(user, settings), render, repo.getTags, repo.getCategories).body)
    }
  }

  private[this] val savePostHandler: HttpHandler = authenticated{ (ex, user) =>
    ex.getRequestReceiver.receiveFullString{
      (ex2: HttpServerExchange, message: String) =>
        SavingData.parseFrom(message) match {
        case None => setResponse(ex2, "save failed", StatusCodes.BAD_REQUEST)
        case Some(data) =>
          val id = repo.savePost(data)
          setResponse(ex2, id.toString, StatusCodes.OK)
      }
    }
  }

  private[this] val updatePostHandler: HttpHandler = authenticated{ (ex, user) =>
    val id = ex.getPathParameters.get("postId").peekFirst().toInt
    ex.getRequestReceiver.receiveFullString{
      (ex2: HttpServerExchange, message: String) =>
        SavingData.parseFrom(message) match {
          case None => setResponse(ex2, "save failed", StatusCodes.BAD_REQUEST)
          case Some(data) =>
            repo.updatePost(id, data)
            setResponse(ex2, "save succeed", StatusCodes.OK)
        }
    }
  }

  private[this] val newTagHandler: HttpHandler = authenticated{ (ex, user) =>
    val tm = ex.getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
    val name = tm.getParameters.get("tagName")
    setResponse(ex, repo.addNewTag(name).toString, StatusCodes.OK)
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
      .map(_.getValue) match {
      case None | Some("") => settings.getUrl("/admin/")
      case Some(x) => x
    }

    maybeUser match {
      case Some(_) =>
        redirect(ex, redirectUrl)
      case None =>
        redirect(ex, settings.loginUrl(redirectUrl, "not_match"))
    }
  }

  val handler: HttpHandler = {
    val handler = Handlers.routing()
      .get("/", handlerForTemplate(admintop.apply))
      .get("/login", loginPageHandler)
      .get("/logout", logoutHandler)
      .get("/new", newPostHandler)
      .get("/edit/{postId}", editPagePostHandler)
      .post("/edit/{postId}", updatePostHandler)
      .post("/edit", savePostHandler)
      .post("/login", formParsed(loginFormHandler))
      .put("/tag/{tagName}", newTagHandler)

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

  private def handlerForTemplate(template: AdminPageDataSet => HtmlFormat.Appendable): HttpHandler = authenticated{ (ex, user) =>
    setResponse(ex, template(new AdminPageDataSet(user, settings)).body)
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
