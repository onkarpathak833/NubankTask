package com.example.nubank.authorize

import java.util

import com.example.nubank.authorize.rules.TransactionRules
import com.example.nubank.domain.{Account, Transaction, TransactionResponse}
import org.json.JSONObject
import com.example.nubank.constants.TransactionConstants._

class TransactionProcessor {
  def initializeAccount(): Unit = {
    Account.accountsList = List()
    Account.availableLimit = 0
  }

  def processTransaction(inputTransaction: String): String = {
    val userTransaction: JSONObject = new JSONObject(inputTransaction)
    val keysIterator: util.Iterator[_] = userTransaction.keys()
    val transactionType = keysIterator.next().toString
    transactionType match {
      case ACCOUNT_TYPE => {
        val accountObject: JSONObject = userTransaction.getJSONObject(transactionType)
        val account: Account = Account(accountObject.getBoolean(ACTIVE_CARD), accountObject.getInt(AVAILABLE_LIMIT))
        val response: TransactionResponse = validateAndCreateAccount(account)
        val responseData = response.createResponse()
        responseData
      }

      case TRANSACTION_TYPE => {
        val transactionObject = userTransaction.getJSONObject(transactionType)
        val transaction = Transaction(transactionObject.getString(MERCHANT), transactionObject.getInt(AMOUNT), transactionObject.getString(TIME))
        val account = Account.accountsList.headOption
        authorize(transaction, account).createResponse()
      }

    }
  }

  private def validateAndCreateAccount(account: Account): TransactionResponse = {
    val accountCreateResponse = account.validateAndCreateAccount()
    if (accountCreateResponse) {
      TransactionResponse(Some(account), List(ACCOUNT_INITIALIZED_ERROR))

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
      transaction.registerTransaction
      val updatedAccount = account.map(acc => acc.updateAccountLimit(accountBalance))
      TransactionResponse(updatedAccount, List.empty[String])

    }

  }
}
