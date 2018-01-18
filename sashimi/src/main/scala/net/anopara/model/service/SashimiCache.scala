package net.anopara.model.service

import net.anopara.model.db.PostTaxonomyData

import scala.concurrent.duration._

class SashimiCache(expiredTimePostSec: Int) {
  import com.github.benmanes.caffeine.cache.Caffeine

  import scalacache._
  import scalacache.caffeine._

  private val underlyingPostCache = Caffeine.newBuilder()
    .maximumSize(10000L)
    .build[String, Entry[PostTaxonomyData]]
  private implicit val postCache: Cache[PostTaxonomyData] = CaffeineCache(underlyingPostCache)

  private val underlyingRenderResultCache = Caffeine.newBuilder()
    .maximumSize(10000L)
    .build[String, Entry[String]]
  private implicit val renderResultCache: Cache[String] = CaffeineCache(underlyingRenderResultCache)

  import scalacache.modes.sync._
  def cachedPost(key: String)(fetchAction: => Option[PostTaxonomyData]): Option[PostTaxonomyData] =
    postCache.get(key) match {
      case None =>
        fetchAction match {
          case None => None
          case Some(x) =>
            postCache.caching(key)(ttl = Some(expiredTimePostSec.seconds))(x)
            Some(x)
        }
      case x => x
    }

  def cachedPostResult(relativePath: String)(fetchAction: => String): String =
    renderResultCache.get(relativePath) match {
      case None =>
        renderResultCache.caching(relativePath)(ttl = Some(expiredTimePostSec.seconds))(fetchAction)
        fetchAction
      case Some(x) => x
    }
}