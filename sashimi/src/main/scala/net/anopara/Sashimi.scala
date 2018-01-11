package net.anopara

import com.typesafe.config.ConfigFactory
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.util.Headers
import io.undertow.{Handlers, Undertow}
import net.anopara.handler.PageHandler
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
      .build()

    server.start()
  }
}