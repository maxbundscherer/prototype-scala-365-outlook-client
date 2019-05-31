package de.maxbundscherer.prototype.outlookclient

import de.maxbundscherer.prototype.outlookclient.services._
import de.maxbundscherer.prototype.outlookclient.utils.Logger

object Main extends Logger("Main") with App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val networkService  : NetworkService = new NetworkService()
  val jsonService     : JsonService    = new JsonService()
  val oAuthService    : OAuthService   = new OAuthService()
  val outlookService  : OutlookService = new OutlookService(networkService, jsonService, oAuthService)

  /* Test Driver */
  outlookService.getUserData  .map(future => log(s"Got (userData) '$future'"))
  outlookService.getMails     .map(future => log(s"Got (mails) '$future'"))
  outlookService.sendMail("Hallo, ich bin die Nachricht!", "Test-Betreff 123", "maximilian@example.de")
                              .map(future => log(s"Got (sendMail) '$future'"))

  Thread.sleep(5000)
  oAuthService.terminateActorSystem()
}