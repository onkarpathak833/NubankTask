package com.example.nubank.domain

import scala.collection.immutable.ListSet
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Transaction(merchant: String, amount: Int, time: String) {
  def registerTransaction = {
    Transaction.transactions.append(Transaction(merchant, amount, time))
  }
}

object Transaction {
  var transactions: ListBuffer[Transaction] = new ListBuffer[Transaction]

}
