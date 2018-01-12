package net.anopara.handler

import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers

trait HandlerBase {
  def setResponse(ex: HttpServerExchange, response: String, responseCode: Int = 200) = {
    ex.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/html")
    ex.setStatusCode(200)
    ex.getResponseSender.send(response)
  }
}
