package net.anopara.auth

import io.getquill.JdbcContextConfig
import io.getquill.util.LoadConfig
import net.anopara.model.SashimiSettings
import org.pac4j.core.config.{Config, ConfigFactory}
import org.pac4j.core.engine.DefaultCallbackLogic
import org.pac4j.http.client.indirect.FormClient
import org.pac4j.sql.profile.service.DbProfileService
import org.pac4j.undertow.context.UndertowWebContext

class AuthConfigFactory(settings: SashimiSettings) extends ConfigFactory {
  override def build(parameters: AnyRef*): Config = {

    val dataSource = JdbcContextConfig(LoadConfig("ctx")).dataSource
    new DbProfileService(dataSource)
    val form = new FormClient(settings.homeUrl + "admin/login", new DbProfileService(dataSource))
    form.setCallbackUrl(settings.homeUrl + "admin/login")
    val config = new Config(settings.homeUrl + "admin/login", form)
    config.setCallbackLogic(new DefaultCallbackLogic[String, UndertowWebContext]())
    config
  }
}
