package net.anopara.model

import net.anopara.model.db.WpPosts

class SashimiSettings(
  val postCacheTime: Int = 60,
  val pageTemplate: (WpPosts) => String,
  val port: Int = 9001,
) {

}
