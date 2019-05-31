package de.maxbundscherer.prototype.outlookclient.services

import de.maxbundscherer.prototype.outlookclient.utils._
import scala.concurrent.ExecutionContext

class OutlookService(
                      networkService: NetworkService,
                      jsonService: JsonService,
                      oAuthService: OAuthService
                    )
                    (implicit executionContext: ExecutionContext) extends Logger("OutlookService") with Configuration {

  import de.maxbundscherer.prototype.outlookclient.models._
  import scala.concurrent.Future

  log(s"Started")

  /**
    * Get user Data
    * @return Future with UserData
    */
  def getUserData: Future[Option[MSUserData]] = {

    networkService
      .getMSRequest("me", oAuthService.getValidAccessToken)
      .map {
        case None => None
        case Some(sth) => jsonService.convertUserData(sth)
      }
  }

  /**
    * Get Mails
    * @return Future with Mails
    */
  def getMails: Future[Option[Vector[MSMail]]] = {

    networkService
      .getMSRequest("me/messages", oAuthService.getValidAccessToken)
      .map {
        case None => None
        case Some(sth) => jsonService.convertMails(sth)
      }
  }

  /**
    * Send mail
    * @param content String
    * @param subject String
    * @param mail String (e-mail)
    * @return Future with boolean (true = success from server / false = failure from server)
    */
  def sendMail(content: String, subject: String, mail: String): Future[Boolean] = {

    /*

    JSON-Request example:

    POST https://graph.microsoft.com/v1.0/me/sendMail
    Content-type: application/json

    {
      "message": {
        "subject": "Meet for lunch?",
        "body": {
          "contentType": "Text",
          "content": "The new cafeteria is open."
        },
        "toRecipients": [
          {
            "emailAddress": {
              "address": "fannyd@contoso.onmicrosoft.com"
            }
          }
        ],
        "ccRecipients": [
          {
            "emailAddress": {
              "address": "danas@contoso.onmicrosoft.com"
            }
          }
        ]
      },
      "saveToSentItems": "false"
    }

     */

    val body: String = "" +
      "" +
      "{  \n" +
      "   \"message\":{  \n" +
      "      \"subject\":\"" + subject + "\",\n" +
      "      \"body\":{  \n" +
      "         \"contentType\":\"Text\",\n" +
      "         \"content\":\"" + content + "\"\n" +
      "      },\n      \"toRecipients\":[  \n" +
      "         {  \n            \"emailAddress\":{  \n" +
      "               \"address\":\"" + mail + "\"\n" +
      "            }\n" +
      "         }\n" +
      "      ]\n" +
      "   },\n" +
      "   \"saveToSentItems\":\"true\"\n" +
      "}" +
      ""

    networkService.postMSRequest("me/sendMail", oAuthService.getValidAccessToken, Some(body))
      .map(m => if(m.isDefined) true else false)
  }

}