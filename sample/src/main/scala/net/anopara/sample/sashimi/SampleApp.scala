package net.anopara.sample.sashimi

import io.getquill.JdbcContextConfig
import io.getquill.util.LoadConfig
import net.anopara.Sashimi
import net.anopara.auth.BCryptEncoder
import net.anopara.model.SashimiSettings
import net.anopara.sample.sashimi.templates.html._
import org.pac4j.core.context.Pac4jConstants
import org.pac4j.core.credentials.password.SpringSecurityPasswordEncoder
import org.pac4j.core.profile.CommonProfile
import org.pac4j.sql.profile.DbProfile
import org.pac4j.sql.profile.service.DbProfileService

object SampleApp extends App {
  Sashimi.start(new SashimiSettings(
    port = 9001,
    pageTemplate = index.apply(_).body,
    notFoundTemplate = s => s"not found: $s"
  ))
}
