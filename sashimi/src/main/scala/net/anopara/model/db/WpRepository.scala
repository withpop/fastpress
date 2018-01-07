package net.anopara.model.db

import io.getquill._
import net.anopara.model.SashimiCache
import net.anopara.util.Serialization
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

class WpRepository(postCacheTime: Int) {

  val redisPool = new JedisPool(new JedisPoolConfig(), "localhost")
  val sashimiCache = new SashimiCache(postCacheTime)

  val ctx = new MysqlJdbcContext(SnakeCase, "ctx")
  import ctx._

  def getPost(year: Int, month: Int, day: Int, name: String): Option[WpPosts] = {
    val q = quote {
      query[WpPosts].filter(p => p.postName == lift(name) && p.postType == "post")
    }
    ctx.run(q).headOption
  }

  def getPage(pageName: String): Option[WpPosts] = {
    sashimiCached(pageName) {
      val q = quote {
        query[WpPosts].filter(p => p.postName == lift(pageName) && p.postType == "page")
      }
      ctx.run(q).headOption
    }
  }

  def sashimiCached(key: String)(fetchAction: => Option[WpPosts]): Option[WpPosts] = {
    sashimiCache.get(key) match {
      case None =>
        fetchAction match {
          case None => None
          case Some(x) =>
            sashimiCache.put(key, x)
            Some(x)
        }
      case x => x
    }
  }

  def redisCached[T](key: String)(fetchAction: => Option[T]): Option[T] = {
    val redis = redisPool.getResource
    val ret = if(redis.exists(key.getBytes))
      Some(Serialization.deserialise[T](redis.get(key.getBytes)))
    else {
      fetchAction match {
        case Some(x) =>
          redis.set(key.getBytes, Serialization.serialise(x))
          redis.expire(key, postCacheTime)
          Some(x)

        case None => None
      }
    }
    redis.close()
    ret
  }

}
