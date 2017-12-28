package net.anopara.model

import net.anopara.model.db.WpPosts

class SashimiSettings(
  val pageTemplate: (WpPosts) => String,
  val port: Int = 9001,
) {

}
