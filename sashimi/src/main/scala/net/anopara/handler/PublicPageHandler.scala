package net.anopara.handler

import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.undertow.util.Headers
import net.anopara.model.{PostType, SashimiSettings}
import net.anopara.model.db.Repository
import net.anopara.model.service.{Renderer, SashimiCache}

class PublicPageHandler(
  settings: SashimiSettings,
  repository: Repository,
  renderer: Renderer,
  cache: SashimiCache
) extends HandlerBase {

  def getHandlerOf(postType: PostType): HttpHandler = Handlers
    .routing()
    .get("/{name}", ex => {
      val name = ex.getQueryParameters.get("id").getFirst
      repository.getRenderData(name, postType) match {
        case Some(x) =>
          // TODO to be configurable
          val body = cache.cachedPostResult(ex.getRelativePath) {
            renderer.renderPage(x)
          }
          setResponse(ex, body)
        case _ =>
          setResponse(ex, renderer.renderNotFoundPage(ex.getRequestPath), 404)
      }
    })
}
