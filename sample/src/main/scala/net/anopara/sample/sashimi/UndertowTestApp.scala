package net.anopara.sample.sashimi

import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.util.Headers
import io.undertow.{Handlers, Undertow}

object UndertowTestApp extends App {
  val server = Undertow.builder()
    .addHttpListener(9003, "localhost")
    .setHandler(Handlers.path()
      .addPrefixPath("/", Handlers.resource(new ClassPathResourceManager(Thread.currentThread().getContextClassLoader, "public")))
      .addExactPath("/", x => {
        x.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/plain")
        x.getResponseSender.send("Hello")
      })
    ).build()
  server.start()
}
