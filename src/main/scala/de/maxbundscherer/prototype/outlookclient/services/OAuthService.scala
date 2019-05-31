package de.maxbundscherer.prototype.outlookclient.services

import de.maxbundscherer.prototype.outlookclient.utils._

class OAuthService extends Logger("OAuthService") with Configuration {

  import com.github.scribejava.apis.MicrosoftAzureActiveDirectory20Api
  import com.github.scribejava.core.builder.ServiceBuilder
  import akka.actor.ActorSystem

  log("Started")

  private val service =

    new ServiceBuilder(Config.OAuth.apiKey)
    .apiSecret(Config.OAuth.apiSecret)
    .defaultScope(Config.OAuth.scope)
    .callback(Config.OAuth.callback)
    .build(MicrosoftAzureActiveDirectory20Api.instance())

  /*
    Save local checkState (to prevent cross-site-attacks and map to e-mail-address)
   */
  private val checkState: String = java.util.UUID.randomUUID().toString
  private val authUrl: String = service.getAuthorizationUrl(checkState)

  private var code: Option[String]              = None
  private implicit val actorSystem: ActorSystem = ActorSystem("actorSystem")

  //Local http-server (to process callback from oAuth-service)
  {
    import akka.http.scaladsl.Http
    import akka.http.scaladsl.model._
    import akka.http.scaladsl.server.Directives._
    import akka.stream.ActorMaterializer

    implicit val materializer = ActorMaterializer()

    val route =
      path("processOAuthCode") {
        get {
          parameters("code", "state") { (code, state) =>
            log(s"Got code '$code' and checkState '$state'")
            if(this.checkState == state) {
              log("Code-Check is valid")
              this.code = Some(code)
            }
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Please see server log</h1><p>Got code '$code'</p><p>Got state '$state'</p>"))
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  }

  log(s"Use authUrl to get code '$authUrl'")

  //Wait for code through local http-server
  while(code.isEmpty) {

    Thread.sleep(1000)
  }

  //TODO: Add try (code can be wrong)
  private var accessToken = service.getAccessToken(code.getOrElse(""))

  log(s"Got accessToken expires in '${accessToken.getExpiresIn}' / '${accessToken.getAccessToken}'")

  /**
    * Get valid access token (auto refresh)
    * @return String
    */
  def getValidAccessToken: String = {

    /**
      * Refresh cycle
      */
    if(accessToken.getExpiresIn <= 100) {
      log("Refresh token now")
      accessToken = service.refreshAccessToken(accessToken.getRefreshToken)
    }

    accessToken.getAccessToken
  }

  /**
    * Terminates local actorSystem (http-server)
    */
  def terminateActorSystem(): Unit = {
    this.actorSystem.terminate()
  }

}