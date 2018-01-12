package net.anopara.handler

import io.undertow.Handlers
import io.undertow.server.HttpHandler
import net.anopara.model.SashimiSettings
import org.pac4j.core.config.Config
import org.pac4j.undertow.handler.{CallbackHandler, SecurityHandler}

class AuthHandler(settings: SashimiSettings, config: Config) extends HandlerBase {

  private val indexPageHandler: HttpHandler = ex => {
    setResponse(ex, "admin page")
  }

  private val loginPageHandler: HttpHandler = ex => {
    setResponse(ex, "login page")
  }

  def authHandler: HttpHandler = {
    Handlers.path()
      .addExactPath("/admin/callback", CallbackHandler.build(config))
      .addExactPath("/admin/index", SecurityHandler.build(indexPageHandler, config))
      .addExactPath("/admin/login", SecurityHandler.build(loginPageHandler, config))
  }
}
