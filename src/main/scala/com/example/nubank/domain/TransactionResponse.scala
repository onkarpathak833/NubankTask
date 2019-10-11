package com.example.nubank.domain
import com.example.nubank.constants.TransactionConstants._

case class TransactionResponse(account: Option[Account], violations: List[String]) {
  def createResponse(): String = {

    val accountInfo: (Any, Any) = account match {
      case Some(x) => (x.ActiveCard, x.AvailableLimit)
      case None => (None, None)
    }

    var response: String = "{\"account\":"
    val accountData = "{\"active-card\":" + accountInfo._1 + ",\"available-limit\":" + accountInfo._2 + "}"
    var violationsString: String = ""

    violations
      .filter(value => value.nonEmpty)
      .foreach(data => violationsString = violationsString + "," + data)

    val violationsData = violationsString.length match {
      case 0 => "\"violations\":" + "[" + violationsString + "]" + "}"
      case _ => {

        violationsString = violationsString.replaceFirst(",", "")
        "\"violations\":" + "[\"" + violationsString + "\"]" + "}"
      }
    }
    response = response + accountData + "," + violationsData
    response
  }
}


