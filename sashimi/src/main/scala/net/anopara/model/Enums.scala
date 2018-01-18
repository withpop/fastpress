package net.anopara.model

sealed abstract class PostType(val dbValue: String)

object PostTypes {
  case object Post extends PostType("post")
  case object Page extends PostType("page")

  def parse(v: String): PostType = v match {
    case "post" => Post
    case "page" => Page
  }
}