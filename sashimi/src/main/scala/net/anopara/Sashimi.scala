package net.anopara

import com.typesafe.config.ConfigFactory
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.server.session.{InMemorySessionManager, SessionAttachmentHandler, SessionCookieConfig}
import io.undertow.util.Headers
import io.undertow.{Handlers, Undertow}
import net.anopara.auth.AuthConfigFactory
import net.anopara.handler.{AuthHandler, PageHandler}
import net.anopara.model.db.Repository
import net.anopara.model.{Renderer, SashimiCache, SashimiSettings}
import org.flywaydb.core.Flyway

object Sashimi {
  def start(settings: SashimiSettings) = {
    val flyway = new Flyway()
    val config = ConfigFactory.load.getConfig("ctx.dataSource")
    flyway.setDataSource(config.getString("url"), config.getString("user"), config.getString("password"))
    flyway.migrate()

    val cache = new SashimiCache(settings.postCacheTime)
    val repository = new Repository(cache)
    val renderer = new Renderer(settings.pageTemplate, settings.notFoundTemplate)
    val authConfig = new AuthConfigFactory(settings).build()

    val server = Undertow.builder()
      .addHttpListener(settings.port, "localhost")
      .setHandler(Handlers.path()
        .addPrefixPath("/assets", Handlers.resource(new ClassPathResourceManager(Thread.currentThread().getContextClassLoader, "static")))
        .addExactPath("/", x => {
          x.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/plain")
          x.getResponseSender.send("Hello")
        })
      )
      .setHandler(PageHandler.basicHandler(settings, repository, renderer, cache))
      .setHandler(new SessionAttachmentHandler(new AuthHandler(settings, authConfig).authHandler,
        new InMemorySessionManager("SessionManager"), new SessionCookieConfig))
      .build()

    server.start()
  }
}