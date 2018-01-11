package net.anopara.handler

import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.undertow.util.Headers
import net.anopara.model.{Renderer, SashimiCache, SashimiSettings}
import net.anopara.model.db.Repository

object PageHandler extends SashimiHandler {

  def basicHandler(settings: SashimiSettings, repository: Repository, renderer: Renderer, cache: SashimiCache): HttpHandler = {
    Handlers.path()
        .addPrefixPath("/post/", ex => {
          repository.getRenderData(ex.getRelativePath.stripPrefix("/")) match {
            case None => setResponse(ex, renderer.renderNotFoundPage(ex.getRequestPath), 404)
            case Some(x) =>
              // TODO to be configurable
              val body = cache.cachedPostResult(ex.getRelativePath) {
                renderer.renderPage(x)
              }
              setResponse(ex, body)
          }
        })
  }
}
