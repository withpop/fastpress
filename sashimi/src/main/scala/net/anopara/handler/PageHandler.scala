package net.anopara.handler

import io.undertow.Handlers
import io.undertow.server.handlers.PathHandler
import io.undertow.util.Headers
import net.anopara.model.SashimiSettings
import net.anopara.model.db.Repository

object PageHandler {


  def basicHandler(settings: SashimiSettings, repository: Repository): PathHandler = {
    Handlers.pathTemplate()
      .add("/item/{itemId}", ex => {
        ex.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/plain")
        ex.getResponseSender.send("Hello")
      })
  }

}
