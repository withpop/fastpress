package net.anopara.model

import net.anopara.model.db.{Post, RenderDataSet}
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class Renderer(
  pageTemplate: (RenderDataSet) => String,
  notFoundTemplate: (String) => String
) {
  val parser = Parser.builder.build
  val renderer = HtmlRenderer.builder.build

  private def renderMarkdown(md: String): String = renderer.render(parser.parse(md))

  def renderPage(post: RenderDataSet): String = {
    renderMarkdown(pageTemplate(post))
  }

  def renderNotFoundPage(requestPath: String): String = {
    notFoundTemplate(requestPath)
  }
}
