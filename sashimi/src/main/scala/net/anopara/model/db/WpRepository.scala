package net.anopara.model.db

import io.getquill._

object WpRepository {
  val ctx = new MysqlJdbcContext(SnakeCase, "ctx")
  import ctx._

  def getPost(year: Int, month: Int, day: Int, name: String): WpPosts = {
    val q = quote {
      query[WpPosts].filter(p => p.postName == lift(name))
    }
    ctx.run(q).headOption.get
  }
}
