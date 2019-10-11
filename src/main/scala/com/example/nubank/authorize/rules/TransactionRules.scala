package com.example.nubank.authorize.rules

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.example.nubank.domain.{Account, Transaction, TransactionResponse}

case class TransactionRules(transaction: Transaction, account: Option[Account]) {

  def run = {
    List(unInitializedAccountRule, cardInactiveRule,
      transactionExceedsAvailableLimitRule,
      noHighFrequencyTransactionsRule, noDoubledTransactions)
      .filter(result => !result._1)
  }

  private def unInitializedAccountRule: (Boolean, TransactionResponse) = {
    if (Account.accountsList.isEmpty) {
      (false, TransactionResponse(account, List("account-not-initialized")))
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def cardInactiveRule = {
    if (!account.exists(_.ActiveCard)) {
      (false, TransactionResponse(account, List("card-not-active")))
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def transactionExceedsAvailableLimitRule = {
    if (transaction.amount > Account.availableLimit) {
      (false, TransactionResponse(account, List("insufficient-limit")))
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def noHighFrequencyTransactionsRule = {
    if (Transaction.transactions.size >= 3) {
      val transactionList = Transaction.transactions.toList
      val currentTransactionTime: Instant = Instant.parse(transaction.time)
      val thirdLastTransaction: Instant = Instant.parse(Transaction.transactions.toList(transactionList.size - 3).time)
      val differenceBetweenCurrentAndThirdLastTransaction = ChronoUnit.MINUTES.between(currentTransactionTime, thirdLastTransaction)

      if (differenceBetweenCurrentAndThirdLastTransaction <= 2) {
        (false, TransactionResponse(account, List("high-frequency-small-interval")))
      }
      else {
        (true, TransactionResponse(account, List()))
      }
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

  private def noDoubledTransactions = {
    if (Transaction.transactions.size >= 1) {
      val transactionList = Transaction.transactions.toList
      val currentTransactionTime = Instant.parse(transaction.time)

      val doubledTransactions = transactionList.
        filter(thisTransaction =>
          ChronoUnit.MINUTES.between(currentTransactionTime, Instant.parse(thisTransaction.time)) <= 2
            && thisTransaction.merchant.equals(transaction.merchant)
            && thisTransaction.amount.equals(transaction.amount)
        )

      if (doubledTransactions.isEmpty) {
        (true, TransactionResponse(account, List()))
      }
      else {
        (false, TransactionResponse(account, List("doubled-transaction")))
      }
    }
    else {
      (true, TransactionResponse(account, List()))
    }
  }

}
