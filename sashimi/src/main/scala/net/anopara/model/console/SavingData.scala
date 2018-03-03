package net.anopara.model.console

import java.time.{LocalDateTime, ZonedDateTime}

import net.anopara.model.db.{Post, PostTaxonomy, Taxonomy}

case class SavingData(
  title: String,
  postType: String,
  category: Long,
  pathName: String,
  tags: Option[List[Long]],
  postedAt: LocalDateTime,
  status: String,
  attribute: String,
  body: String
) {

  def toPost: Post = Post(
    title = title,
    content = body,
    pathName = pathName,
    status = status,
    author = None, // TODO
    postType = postType,
    attribute = attribute,
    postedAt = postedAt,
  )

  def toPostTaxonomy(id: Long): Seq[PostTaxonomy] = {
    val targets = tags match {
      case None => Seq(category)
      case Some(ts) => Seq(category) ++ ts
    }

    targets.map(PostTaxonomy(id, _))
  }
}

object SavingData{
  import io.circe._, io.circe.parser._, io.circe.generic.semiauto._
  import net.anopara.util.CircleEncoderDecoder._
  private implicit val dec: Decoder[SavingData] = deriveDecoder[SavingData]

  def parseFrom(json: String): Option[SavingData] = {
    parse(json).toOption.flatMap(_.as[SavingData].toOption)
  }


}