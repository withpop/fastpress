package net.anopara.model

import net.anopara.model.db.WpPosts
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class Renderer(
  val pageTemplate: (WpPosts) => String,
) {

  val parser = Parser.builder.build
  val renderer = HtmlRenderer.builder.build

  def renderMarkdown(md: String): String = renderer.render(parser.parse(md))

  def renderPage(wpPosts: WpPosts): String = {
    renderMarkdown(pageTemplate(wpPosts))
  }
}
