package net.anopara.auth

import org.mindrot.jbcrypt.BCrypt
import org.pac4j.core.credentials.password.PasswordEncoder

class BCryptEncoder extends PasswordEncoder{

  private val salt = BCrypt.gensalt

  override def encode(password: String): String = BCrypt.hashpw(password, salt)

  override def matches(plainPassword: String, encodedPassword: String): Boolean = BCrypt.checkpw(plainPassword, encodedPassword)
}
