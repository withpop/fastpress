package net.anopara.model.db

import java.time.{LocalDate, LocalDateTime}

import io.getquill._
import net.anopara.model.PostType
import net.anopara.model.service.SashimiCache
import org.mindrot.jbcrypt.BCrypt

class Repository(sashimiCache: SashimiCache) {

  val ctx = new MysqlJdbcContext(SnakeCase, "ctx")
  import ctx._

  private[this] var menuCache = fetchMenu()

  val date = quote {
    (i: LocalDateTime) => infix"DATE($i)".as[LocalDate]
  }

  def getUser(userid: String, pass: String): Option[User] = {
    val q = quote {
      query[User].filter(p => p.id == lift(userid))
    }
    ctx.run(q).headOption match {
      case Some(user) if BCrypt.checkpw(pass, user.password) =>
          Some(user)

      case None =>
        None
    }
  }

  def addUser(user: User) = {
    val q = quote {
      query[User].insert(lift(user))
    }
    ctx.run(q)
  }

  def getRenderData(pathName: String, postType: PostType): Option[RenderDataSet] = {
    val maybeElement = sashimiCache.cachedPost("post/" + pathName) {
      val q = quote {
        query[Post].filter(p => p.pathName == lift(pathName) && p.postType == lift(postType.dbValue))
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
