package net.anopara

import com.typesafe.config.ConfigFactory
import io.undertow.Undertow
import net.anopara.handler.SashimiHandlerBuilder
import net.anopara.model.SashimiSettings
import net.anopara.model.db.Repository
import net.anopara.model.service.{AuthService, Renderer, SashimiCache}
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

object Sashimi {
  private val logger = LoggerFactory.getLogger(Sashimi.getClass)

  def start(settings: SashimiSettings) = {
    // DB Migration
    val flyway = new Flyway()
    val config = ConfigFactory.load.getConfig("ctx.dataSource")
    flyway.setDataSource(config.getString("url"), config.getString("user"), config.getString("password"))
    flyway.migrate()

    // Init models
    val cache = new SashimiCache(settings.postCacheTime)
    val repository = new Repository(cache)
    val renderer = new Renderer(settings.pageTemplate, settings.notFoundTemplate)
    val auth = new AuthService(180, repository) // TODO Configurable

    // Build server
    val server = Undertow.builder()
      .addHttpListener(settings.port, "localhost")
      .setIoThreads(200)
      .setWorkerThreads(400)
      .setHandler(new SashimiHandlerBuilder(settings, cache, repository, renderer, auth).build())
      .build()

    logger.info(s"Sashimi server listening on port ${settings.port}")
    server.start()
  }
}