package com.example.nubank.authorize

import com.example.nubank.domain.Account
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FunSuite, Matchers, WordSpec}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.scalactic.source.Position
import org.scalatest.mockito.MockitoSugar

class TransactionProcessorTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  var authorizer: TransactionProcessor = _

  override def beforeEach(): Unit = {
    authorizer = new TransactionProcessor()
    Account.accountsList = List()
    Account.availableLimit = 0
  }

  ".processTransaction" when {
    "called for account create transaction only once" should {
      "create account if its not created" in {
        val userTransaction = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";
        val firstAccountCreateRespone = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[]}"
        authorizer.processTransaction(userTransaction) shouldBe (firstAccountCreateRespone)
      }
    }

    "called for account create multiple times" should {
      "return appropriate error response" in {

        val userTransaction1 = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}";
        val firstAccountCreateRespone = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[]}"
        authorizer.processTransaction(userTransaction1) shouldBe (firstAccountCreateRespone)

        val userTransaction2 = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
        val duplicateAccountCreateRespone = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[\"account-already-initialized\"]}"
        authorizer.processTransaction(userTransaction2) shouldBe (duplicateAccountCreateRespone)
      }
    }
  }

  ".processTransaction" when {
    "called for a valid transaction" should {
      "complete transaction and return response" in {
        val userTransaction1 = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
        val firstAccountCreateRespone = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[]}"
        authorizer.processTransaction(userTransaction1) shouldBe (firstAccountCreateRespone)
        val userTransaction2 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
        val expectedResult = "{\"account\":{\"active-card\":true,\"available-limit\":80},\"violations\":[]}"
        authorizer.processTransaction(userTransaction2) shouldBe (expectedResult)
      }

    }

    "called for a transaction with insufficient balance" should {
      "return appropriate response" in {
        val userTransaction1 = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
        val firstAccountCreateRespone = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[]}"
        authorizer.processTransaction(userTransaction1) shouldBe (firstAccountCreateRespone)

        val userTransaction2 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 120, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
        val expectedResult = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[\"insufficient-limit\"]}"
        authorizer.processTransaction(userTransaction2) shouldBe (expectedResult)
      }
    }

    "called for a transaction with inactive card" should {
      "return appropriate response" in {

        val userTransaction1 = "{\"account\": {\"active-card\": false, \"available-limit\": 100}}"
        val firstAccountCreateRespone = "{\"account\":{\"active-card\":false,\"available-limit\":100},\"violations\":[]}"
        authorizer.processTransaction(userTransaction1) shouldBe (firstAccountCreateRespone)

        val userTransaction2 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 80, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
        val expectedResult = "{\"account\":{\"active-card\":false,\"available-limit\":100},\"violations\":[\"card-not-active\"]}"
        authorizer.processTransaction(userTransaction2) shouldBe (expectedResult)

      }
    }

    "called for uninitialized account" should {
      "return appropriate response" in {
        val userTransaction2 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 80, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
        val expectedResult = "{\"account\":{\"active-card\":None,\"available-limit\":None},\"violations\":[\"account-not-initialized\"]}"
        authorizer.processTransaction(userTransaction2) shouldBe (expectedResult)
      }
    }

    "called for high frequency small interval transactions" should {
      "return appropriate response" in {
        val userTransaction1 = "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
        val firstAccountCreateRespone = "{\"account\":{\"active-card\":true,\"available-limit\":100},\"violations\":[]}"
        authorizer.processTransaction(userTransaction1) shouldBe (firstAccountCreateRespone)

        val userTransaction2 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
        val expectedResult2 = "{\"account\":{\"active-card\":true,\"available-limit\":80},\"violations\":[]}"
        authorizer.processTransaction(userTransaction2) shouldBe (expectedResult2)

        val userTransaction3 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 10, \"time\": \"2019-02-13T10:01:00.000Z\"}}"
        val expectedResult3 = "{\"account\":{\"active-card\":true,\"available-limit\":70},\"violations\":[]}"
        authorizer.processTransaction(userTransaction3) shouldBe (expectedResult3)

        val userTransaction4 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 10, \"time\": \"2019-02-13T10:01:30.000Z\"}}"
        val expectedResult4 = "{\"account\":{\"active-card\":true,\"available-limit\":60},\"violations\":[]}"
        authorizer.processTransaction(userTransaction3) shouldBe (expectedResult4)

        val userTransaction5 = "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 10, \"time\": \"2019-02-13T10:01:50.000Z\"}}"
        val expectedResult5 = "{\"account\":{\"active-card\":true,\"available-limit\":60},\"violations\":[\"high-frequency-small-interval\"]}"
        authorizer.processTransaction(userTransaction5) shouldBe (expectedResult5)
      }
    }
  }


}
