package com.example.nubank

import com.example.nubank.authorize.TransactionProcessor

object MyApplication {

  def main(args: Array[String]): Unit = {
    val transactionAuthorizer = new TransactionProcessor();
    while (true) {
      val userTransaction = scala.io.StdIn.readLine();
      val output = transactionAuthorizer.processTransaction(userTransaction)
      println(output)
    }

  }


}
