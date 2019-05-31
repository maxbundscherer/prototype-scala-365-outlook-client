package de.maxbundscherer.prototype.outlookclient.services

import de.maxbundscherer.prototype.outlookclient.utils._
import scala.concurrent.ExecutionContext

class NetworkService()(implicit executionContext: ExecutionContext) extends Logger("NetworkService") with Configuration {

  import scala.concurrent.Future
  import scala.util.{Try, Success, Failure}
  import com.softwaremill.sttp._

  log("Started")

  /**
    * STTP-Backend init
    */
  private lazy implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  /**
    * Get request
    * @param url String (example https://google.de)
    * @param headers Map (Key->Value)
    * @return Future with optional String (Body)
    */
  private def getRequest(url: String, headers: Map[String, String])(): Future[Option[String]] = Future {

    val request   = sttp
      .headers(headers)
      .get( uri"$url" )

    Try {

      request.send()

    } match {

      case Success(success) =>

        if(success.body.isRight) Some(success.body.right.get) else {
          log(s"Got failure in request '${success.body}'")
          None
        }

      case Failure(failure)       =>

        log(s"Got failure in request '$failure'")
        None

    }

  }

  /**
    * Post request
    * @param url String (example https://google.de)
    * @param body Optional String
    * @param headers Map (Key->Value)
    * @return Future with optional String (Body)
    */
  private def postRequest(url: String, body: Option[String], headers: Map[String, String])(): Future[Option[String]] = Future {

    val request   = sttp
      .headers(headers)
      .body(body.getOrElse(""))
      .post( uri"$url" )

    Try {

      request.send()

    } match {

      case Success(success) =>

        if(success.body.isRight) Some(success.body.right.get) else {
          log(s"Got failure in request '${success.body}'")
          None
        }

      case Failure(failure)       =>

        log(s"Got failure in request '$failure'")
        None

    }

  }

  /**
    * Get request (prepared for microsoft requests)
    * @param postfixUrl String (example 'me')
    * @param accessToken String
    * @param headers Map (Key->Value)
    * @return Future with optional String (Body)
    */
  def getMSRequest(postfixUrl: String, accessToken: String, headers: Map[String, String] = Map.empty)(): Future[Option[String]] = {

    getRequest(Config.Microsoft.baseUrl + Config.Microsoft.version + "/" + postfixUrl, headers + (
      "Accept" -> "application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8",
      "Authorization" -> accessToken
      ))
  }

  /**
    * Post request
    * @param postfixUrl String (example 'me/sendMail')
    * @param accessToken String
    * @param body Optional String
    * @param headers Map (Key->Value)
    * @return Future with optional String (Body)
    */
  def postMSRequest(postfixUrl: String, accessToken: String, body: Option[String], headers: Map[String, String] = Map.empty)(): Future[Option[String]] = {

    postRequest(Config.Microsoft.baseUrl + Config.Microsoft.version + "/" + postfixUrl, body, headers + (
      //"Accept" -> "application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8", (not needed in here)
      "Content-Type" -> "application/json",
      "Authorization" -> accessToken
    ))
  }

}