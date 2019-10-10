package com.example.nubank.authorize

import org.scalatest.{BeforeAndAfter, FunSuite, Matchers, WordSpec}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.scalactic.source.Position
import org.scalatest.mockito.MockitoSugar

class TransactionProcessorTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfter {

  ".authorize" when {
    "called for account transaction" should {
      "create account if its not created" in {
        val authorizer = new TransactionProcessor()
        val userTransaction = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";
        val expectedRespone = "{\"account\": {\"active-card\": true, \"available-limit\": 100}, \"violations\": []}"
        authorizer.processTransaction(userTransaction) should be(expectedRespone)
      }
    }
  }

}
