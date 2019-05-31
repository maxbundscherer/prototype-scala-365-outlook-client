package de.maxbundscherer.prototype.outlookclient.services

import de.maxbundscherer.prototype.outlookclient.utils._

class JsonService extends Logger("JsonService") {

  import de.maxbundscherer.prototype.outlookclient.models._
  import io.circe._, io.circe.parser._
  import io.circe.Decoder.Result

  log("Started")

  /**
    * Converts string to json
    * @param data String
    * @return Option with Json
    */
  private def convertStringToJson(data: String): Option[Json] = {

    parse(data) match {
      case Left(_)      => None
      case Right(right) => Some(right)
    }
  }

  /**
    * Converts json result to string
    * @param data Result
    * @return Option with String
    */
  private def convertResultToString(data: Result[String]): Option[String] = {

    data match {
      case Left(_)      => None
      case Right(right) => Some(right)
    }
  }

  def convertUserData(data: String): Option[MSUserData] = {

    convertStringToJson(data) match {

      case None       => None
      case Some(json) =>

        val res = MSUserData(
          displayName = convertResultToString(json.hcursor.get[String]("displayName")),
          givenName   = convertResultToString(json.hcursor.get[String]("givenName")),
          mail        = convertResultToString(json.hcursor.get[String]("mail"))
        )

        Some(res)
    }
  }

  def convertMails(data: String): Option[Vector[MSMail]] = {

    convertStringToJson(data) match {

      case None       => None
      case Some(json) =>

        json.hcursor.downField("value").values match {

          case None             => None
          case Some(jsonValues) =>

            Some(
              jsonValues.toVector.map(jsonEntity =>
                MSMail(
                  id            = convertResultToString(jsonEntity.hcursor.get[String]("id")),
                  subject       = convertResultToString(jsonEntity.hcursor.get[String]("subject")),
                  contentType   = convertResultToString(jsonEntity.hcursor.downField("body").get[String]("contentType")),
                  content       = convertResultToString(jsonEntity.hcursor.downField("body").get[String]("content")),
                  fromMail      = convertResultToString(jsonEntity.hcursor.downField("from").downField("emailAddress").get[String]("address")),
                  fromName      = convertResultToString(jsonEntity.hcursor.downField("from").downField("emailAddress").get[String]("name"))
                )
              )
            )

        }

    }
  }

}