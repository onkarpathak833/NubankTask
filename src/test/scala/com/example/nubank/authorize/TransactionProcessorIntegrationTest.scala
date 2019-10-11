package com.example.nubank.authorize

import com.example.nubank.domain.{Account, Transaction}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatest.mockito.MockitoSugar

import scala.collection.mutable.ListBuffer
import scala.io.Source

class TransactionProcessorIntegrationTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {
  var authorizer: TransactionProcessor = _

  override def beforeEach() = {
    authorizer = new TransactionProcessor()
    Account.accountsList = List()
    Account.availableLimit = 0
    Transaction.transactions = new ListBuffer[Transaction]
  }

  ".processTransaction" when {
    "called for input operations file" should {
      "return expected transaction response" in {
        val fileStream = getClass.getResourceAsStream("/operations.txt")
        val outputStream = getClass.getResourceAsStream("/output.txt")
        val fileLines = Source.fromInputStream(fileStream).getLines().toList
        val outputLines = Source.fromInputStream(outputStream).getLines().toList

        for (i <- 0 to fileLines.length - 1) {
          val inputLine = fileLines(i)
          val outputLine = outputLines(i)
          authorizer.processTransaction(inputLine) shouldBe (outputLine)
        }

      }
    }
  }

}
