package net.anopara.model

import net.anopara.model.db.{Post, RenderDataSet}

class SashimiSettings(
  val postCacheTime: Int = 60,
  val pageTemplate: (RenderDataSet) => String,
  val notFoundTemplate: (String) => String,
  val port: Int = 9001,
) {

}
