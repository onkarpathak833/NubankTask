package com.example.nubank.authorize

import java.util

import com.example.nubank.authorize.rules.TransactionRules
import com.example.nubank.domain.{Account, Transaction, TransactionResponse}
import com.google.gson.{FieldNamingPolicy, Gson, GsonBuilder}
import javafx.scene.input.KeyCombination.Modifier
import org.json.JSONObject

class TransactionProcessor {

  val gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create()

  def initializeAccount(): Unit = {
    Account.accountsList = List()
    Account.availableLimit = 0
  }

  def processTransaction(inputTransaction: String): String = {
    val userTransaction: JSONObject = new JSONObject(inputTransaction)
    val keysIterator: util.Iterator[_] = userTransaction.keys()
    val transactionType = keysIterator.next().toString
    transactionType match {
      case "account" => {
        val accountObject: JSONObject = userTransaction.getJSONObject(transactionType)
        val account: Account = Account(accountObject.getBoolean("active-card"), accountObject.getInt("available-limit"))
        val response: TransactionResponse = validateAndCreateAccount(account)
        val responseData = response.createResponse()
        responseData
      }

      case "transaction" => {
        val transactionObject = userTransaction.getJSONObject(transactionType)
        val transaction = Transaction(transactionObject.getString("merchant"), transactionObject.getInt("amount"), transactionObject.getString("time"))
        val account = Account.accountsList.headOption
        authorize(transaction, account).createResponse()
      }

    }
  }

  private def validateAndCreateAccount(account: Account): TransactionResponse = {
    val accountCreateResponse = account.validateAndCreateAccount()
    if (accountCreateResponse) {
      TransactionResponse(Some(account), List("account-already-initialized"))

    }
    else {
      TransactionResponse(Some(account), List.empty[String])
    }

  }

  private def authorize(transaction: Transaction, account: Option[Account]): TransactionResponse = {
    val failedRules = TransactionRules(transaction, account).run
    if (failedRules.nonEmpty) {
      failedRules.map(_._2).head
    }
    else {
      val accountBalance = Account.availableLimit - transaction.amount
      Account.availableLimit = accountBalance
      TransactionResponse(Some(Account(true, accountBalance)), List.empty[String])
    }

  }
}
