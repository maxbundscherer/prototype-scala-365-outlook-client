package de.maxbundscherer.prototype.outlookclient.utils

trait Configuration {

  import com.typesafe.config.ConfigFactory

  object Config {

    private lazy val config           = ConfigFactory.load("dev-application.conf")
    private lazy val oAuthConfig      = config.getConfig("oAuth")
    private lazy val microsoftConfig  = config.getConfig("microsoft")

    object OAuth {
      lazy val apiKey: String       = oAuthConfig.getString("apiKey")
      lazy val apiSecret: String    = oAuthConfig.getString("apiSecret")
      lazy val scope: String        = oAuthConfig.getString("scope")
      lazy val callback: String     = oAuthConfig.getString("callback")
    }

    object Microsoft {
      lazy val baseUrl: String       = microsoftConfig.getString("baseUrl")
      lazy val version: String       = microsoftConfig.getString("version")
    }

  }

}