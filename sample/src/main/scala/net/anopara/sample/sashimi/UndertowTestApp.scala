package net.anopara.sample.sashimi

import io.undertow.util.Headers
import io.undertow.{Handlers, Undertow}

object UndertowTestApp extends App {
  val server = Undertow.builder()
    .addHttpListener(9003, "localhost")
    .setHandler(Handlers.path()
      .addExactPath("/", x => {
        x.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/plain")
        x.getResponseSender.send("Hello")
      })
      // .addPrefixPath("/", Handlers.resource(ClassPathResourceManager(Thread.currentThread().contextClassLoader, "public")))
    ).build()
  server.start()
}
