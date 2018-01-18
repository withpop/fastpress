package net.anopara.auth

import org.mindrot.jbcrypt.BCrypt

class BCryptEncoder{

  private val salt = BCrypt.gensalt

  def encode(password: String): String = BCrypt.hashpw(password, salt)

  def matches(plainPassword: String, encodedPassword: String): Boolean = BCrypt.checkpw(plainPassword, encodedPassword)
}
