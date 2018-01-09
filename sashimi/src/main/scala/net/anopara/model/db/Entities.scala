package net.anopara.model.db

import java.time.LocalDateTime

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
)

case class Taxonomy(
  id: Long,
  parentId: Long,
  taxoType: String,
  link: String
)