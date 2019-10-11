package com.example.nubank

import java.io.File

import scala.io.{BufferedSource, Source}
import com.example.nubank.authorize.TransactionProcessor

object MyApplication {

  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      throw new Exception("No transactions/operations file provided.")
    }

    val filePath = args(0)
    val file = new File(filePath)
    if (!file.exists()) {
      throw new Exception("Input transactions/operations file does not exist at given path!")
    }

    val fileSource: BufferedSource = Source.fromFile(file)

    val transactionAuthorizer = new TransactionProcessor();
    transactionAuthorizer.initializeAccount()
    for (line <- fileSource.getLines()) {
      val response = transactionAuthorizer.processTransaction(line)
      println(response)
    }

    //    while (true) {
    //      val userTransaction = scala.io.StdIn.readLine();
    //      val output = transactionAuthorizer.processTransaction(userTransaction)
    //      println(output)
    //    }

  }


}
