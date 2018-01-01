package net.anopara.model.db

case class WpPosts(
  id: Long,
  postAuthor: String,
  postTitle: String,
  postContent: String,
  postStatus: String,
  postName: String,
  postType: String,
) extends Serializable