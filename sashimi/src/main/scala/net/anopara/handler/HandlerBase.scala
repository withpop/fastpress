package net.anopara.handler

import io.undertow.server.HttpServerExchange
import io.undertow.util.{Headers, StatusCodes}

trait HandlerBase {
  def setResponse(ex: HttpServerExchange, response: String, responseCode: Int = StatusCodes.OK): Unit = {
    ex.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/html")
    ex.setStatusCode(responseCode)
    ex.getResponseSender.send(response)
  }

  def redirect(ex: HttpServerExchange, redirect: String, statusCode: Int = StatusCodes.FOUND): Unit = {
    ex.setStatusCode(statusCode)
    ex.getResponseHeaders.put(Headers.LOCATION, redirect)
  }
}
