package net.anopara.model.service

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.Caffeine
import io.undertow.server.handlers.form.FormData
import net.anopara.model.db.{Repository, User}

class AuthService(ttlMinutes: Int, repo: Repository) {

  import scalacache._
  import scalacache.caffeine._

  private val underlyingSessionCache = Caffeine.newBuilder()
      .expireAfterAccess(ttlMinutes, TimeUnit.MINUTES)
      .build[String, Entry[User]]
  private implicit val sessionCache: Cache[User] = CaffeineCache(underlyingSessionCache)

  import scalacache.modes.sync._
  def getUserFromSession(sessionId: String): Option[User] = sessionCache.get(sessionId) match {
    case x: Option[User] => x
  }

  def authenticate(userid: String, pass: String, sessionId: String): Option[User] = {
    repo.getUser(userid, pass) match {
      case Some(x) =>
        sessionCache.put(sessionId)(x)
        Some(x)
      case None =>
        None
    }
  }
}
