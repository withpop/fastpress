package net.anopara.model

import java.util.concurrent.ConcurrentHashMap

import net.anopara.model.db.WpPosts
import org.joda.time.DateTime

class SashimiCache(expiredTimePostSec: Int) {
  private[this] val map = new ConcurrentHashMap[String, CacheValue[WpPosts]]()
  private[this] val expiredTimePostMillis = expiredTimePostSec * 1000

  class CacheValue[T](val value: T, val createdTime: Long = System.currentTimeMillis())


  def put(key: String, value: WpPosts) = {
    map.put(key, new CacheValue[WpPosts](value))
  }

  def get(key: String): Option[WpPosts] = {
    map.get(key) match {
      case null => None
      case x if (System.currentTimeMillis() - x.createdTime) > expiredTimePostMillis =>
        None
      case x => Some(x.value)
    }
  }
}
