package net.anopara.model.asset

import java.io.InputStream
import java.net.URLConnection

class Asset(val mimeType: String, val stream: InputStream)
class AssetsResource(extPath: String) {
  private[this] val mimes = URLConnection.getFileNameMap
  def getFromResource(path: String): Option[Asset] = {
    getClass.getResourceAsStream("/static/" + path) match {
      case null => None
      case stream =>
        val ct = mimes.getContentTypeFor(path)
        Some(new Asset(ct, stream))
    }
  }
}
