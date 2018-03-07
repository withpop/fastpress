package net.anopara.model.db

import java.time.{LocalDate, LocalDateTime}

import io.getquill._
import net.anopara.model.PostType
import net.anopara.model.console.SavingData
import net.anopara.model.service.SashimiCache
import org.mindrot.jbcrypt.BCrypt

class Repository(sashimiCache: SashimiCache) {


  val ctx = new MysqlJdbcContext(SnakeCase, "ctx")
  import ctx._

  private[this] var menuCache = fetchMenu()
  private[this] var tagCache = fetchTag()
  private[this] var categoryCache = fetchCategory()

  val date = quote {
    (i: LocalDateTime) => infix"DATE($i)".as[LocalDate]
  }

  def addNewTag(name: String): Long = {
    val t = Taxonomy(name = name, taxoType = "tag")
    val p = quote {
      query[Taxonomy].insert(lift(t)).returning(_.id)
    }
    val r = ctx.run(p)
    refreshTaxonomy()
    r
  }

  def addNewCategory(name: String): Long = {
    val t = Taxonomy(name = name, taxoType = "category")
    val p = quote {
      query[Taxonomy].insert(lift(t)).returning(_.id)
    }
    val r = ctx.run(p)
    refreshTaxonomy()
    r
  }

  def addNewMenu(t: Taxonomy): Long = {
    val p = quote {
      query[Taxonomy].insert(lift(t)).returning(_.id)
    }
    val r = ctx.run(p)
    refreshTaxonomy()
    r
  }

  def deleteTaxonomy(id: Long) = {
    val p = quote {
      query[Taxonomy].filter(_.id == lift(id)).delete
    }
    ctx.run(p)
    refreshTaxonomy()
  }


  def updatePost(id: Long, data: SavingData): Unit = {
    ctx.transaction {
      val p = quote {
        val p = lift(data.toPost)
        val i = lift(id)
        infix"""update post set title = ${p.title}, content = ${p.content}, path_name = ${p.pathName},
                status = ${p.status}, post_type = ${p.postType}, attribute = ${p.attribute}, posted_at = ${p.postedAt}
                where post.id = $i
           """.as[Update[Long]]
      }
      ctx.run(p)

      ctx.run(quote {
        query[PostTaxonomy].filter(_.postId == lift(id)).delete
      })

      ctx.run(quote {
        liftQuery(data.toPostTaxonomy(id)).foreach(e => query[PostTaxonomy].insert(e))
      })
    }
  }

  def savePost(data: SavingData): Long = {
    ctx.transaction {
      val p = quote {
        val p = lift(data.toPost)
        infix"""insert into post(title, content, path_name, status, post_type, attribute, posted_at)
             value (${p.title}, ${p.content}, ${p.pathName}, ${p.status}, ${p.postType}, ${p.attribute}, ${p.postedAt})
           """.as[Insert[Long]]
      }
      ctx.run(p)
      val newPostId = ctx.run(quote{
        infix"SELECT LAST_INSERT_ID()".as[Query[Long]]
      }).head

      val pt = quote {
        liftQuery(data.toPostTaxonomy(newPostId)).foreach(e => query[PostTaxonomy].insert(e))
      }
      ctx.run(pt)

      newPostId
    }
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

  def getPost(postId: Long): Option[Post] = {
    val q = quote {
      query[Post].filter(_.id == lift(postId))
    }
    ctx.run(q).headOption
  }

  def getRenderDataNonCache(postId: Long): Option[RenderDataSet] = {
    val maybeElement = {
      val q = quote {
        query[Post].filter(p => p.id == lift(postId))
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
  def getFlatMenu: List[Menu] = menuCache
  def getTags: List[Tag] = tagCache
  def getCategories: List[Category] = categoryCache

  def refreshTaxonomy(): Unit ={
    menuCache = fetchMenu()
    tagCache = fetchTag()
    categoryCache = fetchCategory()
  }

  private def fetchFlatMenu(): List[Menu] = {
    val q = quote {
      for{
        (t, pt) <- query[Taxonomy].filter(_.taxoType == "menu").join(query[PostTaxonomy]).on(_.id == _.taxonomyId)
        p <- query[Post].leftJoin(_.id == pt.postId)
      } yield (t, p)
    }

    ctx.run(q).map{case (t, p) =>
      new Menu(t.id, t.parentId, t.name, t.link.fold(p.map( x => x.url ))(x => Some(x)))
    }
  }

  private def fetchMenu(): List[Menu] = {
    val menus = fetchFlatMenu()
    val list = menus.filter(_.parentId == 0)
    list.foreach{ t =>
      list
        .find(_.id == t.parentId)
        .map(x => x.subMenu += t )
    }

    list
  }

  private def fetchTag(): List[Tag] = {
    val q = quote {
      query[Taxonomy].filter(_.taxoType == "tag")
    }
    ctx.run(q).map( x => new Tag(x.id, x.name))
  }

  private def fetchCategory(): List[Category] = {
    val q = quote {
      query[Taxonomy].filter(_.taxoType == "category")
    }
    ctx.run(q).map( x => new Category(x.id, x.name))
  }

}
