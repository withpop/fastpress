package net.anopara.model

import java.net.URLEncoder

import net.anopara.model.db.{Post, RenderDataSet}

class SashimiSettings(
  val postCacheTime: Int = 60,
  val pageTemplate: (RenderDataSet) => String,
  val notFoundTemplate: (String) => String,
  val port: Int = 9001,
  val hostname: String = "localhost"
) {
  val homeUrl: String = s"http://$hostname:$port/"
  val loginUrl: String = homeUrl + "admin/login"
  def getUrl(url: String): String = homeUrl + url.stripPrefix("/")
  def loginUrl(redirect: String): String = homeUrl + "admin/login?redirect=" + URLEncoder.encode(redirect, "UTF-8")
  def loginUrl(redirect: String, error: String): String = homeUrl + s"admin/login?error=$error&redirect=" + URLEncoder.encode(redirect, "UTF-8")
}
