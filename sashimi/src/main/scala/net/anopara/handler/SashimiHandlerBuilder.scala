package net.anopara.handler

import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.util.Headers
import net.anopara.model.{PostTypes, SashimiSettings}
import net.anopara.model.db.{Repository, User}
import net.anopara.model.service.{AuthService, Renderer, SashimiCache}
import org.mindrot.jbcrypt.BCrypt

class SashimiHandlerBuilder(
  settings: SashimiSettings,
  cache: SashimiCache,
  repo: Repository,
  renderer: Renderer,
  auth: AuthService
) {

  private[this] val publicPageHandler = new PublicPageHandler(settings, repo, renderer, cache)
  private[this] val adminPageHandler: AdminPageHandler = new AdminPageHandler(settings, auth, repo)

  def build(): HttpHandler = {
    Handlers.path()
      // Resources
      .addPrefixPath("/assets", Handlers.resource(new ClassPathResourceManager(Thread.currentThread().getContextClassLoader, "static")))

      // TODO Index
      .addExactPath("/",
      x => {
        x.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/plain")
        x.getResponseSender.send("Hello")
        //repo.addUser(User("debug", "dev user", BCrypt.hashpw("pass", BCrypt.gensalt()), "")) // TODO debug code
      })

      // Public
      .addPrefixPath("/post", publicPageHandler.getHandlerOf(PostTypes.Post))
      .addPrefixPath("/page", publicPageHandler.getHandlerOf(PostTypes.Page))

      // Admin
      .addPrefixPath("/admin", adminPageHandler.handler)

    // TODO plugin additional handlers
  }







}
