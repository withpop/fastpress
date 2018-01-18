package net.anopara.model.db

import java.time.LocalDateTime

import net.anopara.model.SashimiSettings

import scala.collection.mutable

case class Post(
  id: Long,
  title: String,
  content: String,
  pathName: String,
  status: String,
  author: String,
  postType: String,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
) {
  lazy val url: String = postType match {
    case "post" => "/post/" + pathName
    case "page" => "/page/" + pathName
  }
}

case class PostTaxonomy(
  postId: Long,
  taxonomyId: Long
)

case class Taxonomy(
  id: Long,
  parentId: Long,
  name: String,
  taxoType: String,
  link: Option[String]
)

class PostTaxonomyData(
  val post: Post,
  val taxonomies: List[Taxonomy]
) {
  lazy val tags: List[Tag] = taxonomies.filter(_.taxoType == "tag").map(new Tag(_))
  lazy val category: Option[Category] = taxonomies.find(_.taxoType == "category").map(new Category(_))
}

class AdminPageDataSet(
  val user: User,
  val settings: SashimiSettings,
  val attribute: mutable.HashMap[String, Any] = mutable.HashMap()
) {
  def addAttr(key: String, value: Any) = attribute += key -> value
  def getAttrAs[T](key: String, value: Option[T]) = attribute.get(key).map(_.asInstanceOf[T])
  def route(url: String) = settings.getUrl(url)
}

class RenderDataSet(
  val post: Post,
  val menus: List[Menu],
  val tags: List[Tag],
  val category: Option[Category]
)

class Menu(
  val id: Long,
  val parentId: Long,
  val name: String,
  val linkUrl: Option[String],
  val subMenu: mutable.ListBuffer[Menu] = mutable.ListBuffer(),
) {
  def this(taxonomy: Taxonomy){
    this(taxonomy.id, taxonomy.parentId, taxonomy.name, taxonomy.link)
  }
}

class Category(
  val id: Long,
  val name: String
) {
  def this(taxonomy: Taxonomy){
    this(taxonomy.id, taxonomy.name)
  }
  lazy val url: String = "/category/" + name
}

class Tag(
  val id: Long,
  val name: String
) {
  def this(taxonomy: Taxonomy){
    this(taxonomy.id, taxonomy.name)
  }
  lazy val url: String = "/tag/" + name
}

case class User(
  id: String,
  userName: String,
  password: String,
  property: String
) {
  import io.circe._, io.circe.parser._
  lazy val propertyJson: Json = parse(property).right.get // TODO error handling
}