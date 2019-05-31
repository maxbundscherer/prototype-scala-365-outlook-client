package de.maxbundscherer.prototype.outlookclient.models

case class MSUserData(
                     displayName: Option[String],
                     givenName: Option[String],
                     mail: Option[String],
                   )

case class MSMail(
                     subject: Option[String],
                     id: Option[String],
                     contentType: Option[String],
                     content: Option[String],
                     fromName: Option[String],
                     fromMail: Option[String]
                   ) {
  override def toString: String = "\n[" +
    s"subject='$subject' / " +
    s"fromName='$fromName' / " +
    s"fromMail='$fromMail' / " +
    s"contentType='$contentType' / " +
    s"contentDefined='${content.isDefined}' / " +
    s"idDefined='${id.isDefined}'" +
    "]"
}