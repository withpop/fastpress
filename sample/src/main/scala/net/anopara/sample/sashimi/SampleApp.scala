package net.anopara.sample.sashimi

import net.anopara.Sashimi
import net.anopara.model.SashimiSettings
import net.anopara.sample.sashimi.templates.html._

object SampleApp extends App {
  Sashimi.start(new SashimiSettings(
    port = 9001,
    pageTemplate = index.apply(_).body,
    notFoundTemplate = s => s"not found: $s"
  ))
}
