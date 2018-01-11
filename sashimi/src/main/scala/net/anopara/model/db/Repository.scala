package net.anopara.model.db

import java.time.{LocalDate, LocalDateTime}

import io.getquill._
import net.anopara.model.SashimiCache

class Repository(sashimiCache: SashimiCache) {

  val ctx = new MysqlJdbcContext(SnakeCase, "ctx")
  import ctx._

  private[this] var menuCache = fetchMenu()

  val date = quote {
    (i: LocalDateTime) => infix"DATE($i)".as[LocalDate]
  }

  def getRenderData(pathName: String): Option[RenderDataSet] = {
    val maybeElement = sashimiCache.cachedPost("post/" + pathName) {
      val q = quote {
        query[Post].filter(p => p.pathName == lift(pathName) && p.postType == "post")
      }
      val maybePost = ctx.run(q).headOption
      maybePost.map{
        p =>
          val q = quote {
            query[Taxonomy].join(query[PostTaxonomy]).on(_.id == _.taxonomyId)
              .filter(_._2.postId == lift(p.id))
              .map(_._1)
          }
          new PostTaxonomyData(p, ctx.run(q))
      }
    }

    maybeElement.map(pt => new RenderDataSet(pt.post, menuCache, pt.tags, pt.category))
  }

  def getMenu: List[Menu] = menuCache

  private def fetchMenu(): List[Menu] = {
    val q = quote {
      for{
        (t, pt) <- query[Taxonomy].join(query[PostTaxonomy]).on(_.id == _.taxonomyId)
        p <- query[Post].leftJoin(_.id == pt.postId)
      } yield (t, p)
    }

    val menus = ctx.run(q).map{case (t, p) =>
      new Menu(t.id, t.parentId, t.name, t.link.fold(p.map( x => x.url ))(x => Some(x)))
    }
    val list = menus.filter(_.parentId == 0)
    list.foreach{ t =>
      list
        .find(_.id == t.parentId)
        .map(x => x.subMenu += t )
    }

    list
  }

}
